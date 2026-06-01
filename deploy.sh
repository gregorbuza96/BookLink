#!/usr/bin/env bash
#
# Build all BookLink images for the k3s node and push them to ECR.
#
# The k3s node runs on a t4g (ARM/Graviton) instance, so images MUST be
# linux/arm64. This script uses `docker buildx` to build for arm64 and push
# in one step (works from an Intel or Apple-Silicon Mac via QEMU/buildx).
#
# Usage:
#   ./deploy.sh                  # auto-detect region/registry from terraform output
#   REGISTRY=123.dkr.ecr.eu-central-1.amazonaws.com REGION=eu-central-1 ./deploy.sh
#   TAG=v1 ./deploy.sh           # custom tag (default: latest)
#
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT"

# ── Resolve registry + region ─────────────────────────────
if command -v terraform >/dev/null 2>&1 && [[ -d terraform ]]; then
  REGISTRY="${REGISTRY:-$(terraform -chdir=terraform output -raw ecr_registry 2>/dev/null || true)}"
  REGION="${REGION:-$(terraform -chdir=terraform output -raw region 2>/dev/null || true)}"
fi
: "${REGION:=eu-central-1}"
if [[ -z "${REGISTRY:-}" ]]; then
  echo "ERROR: set REGISTRY=<acct>.dkr.ecr.<region>.amazonaws.com (or run after 'terraform apply')." >&2
  exit 1
fi

TAG="${TAG:-latest}"
PLATFORM="linux/arm64"
BACKEND_SERVICES=(config-server api-gateway user-service hotel-service booking-service)

echo "Registry : $REGISTRY"
echo "Region   : $REGION"
echo "Tag      : $TAG"
echo "Platform : $PLATFORM"
echo

# ── Build backend JARs (needed by the Dockerfiles' COPY step) ──
echo "==> Building backend JARs (mvn package)"
mvn -q package -DskipTests

# ── ECR login + buildx setup ──────────────────────────────
echo "==> Logging in to ECR"
aws ecr get-login-password --region "$REGION" | docker login --username AWS --password-stdin "$REGISTRY"

docker buildx inspect booklink >/dev/null 2>&1 || docker buildx create --name booklink --use >/dev/null
docker buildx use booklink

build_push() {
  local name="$1" ctx="$2"
  echo "==> $name"
  aws ecr describe-repositories --repository-names "booklink/$name" --region "$REGION" >/dev/null 2>&1 \
    || aws ecr create-repository --repository-name "booklink/$name" --region "$REGION" >/dev/null
  docker buildx build --platform "$PLATFORM" \
    -t "$REGISTRY/booklink/$name:$TAG" --push "$ctx"
}

for s in "${BACKEND_SERVICES[@]}"; do build_push "$s" "./$s"; done
build_push frontend ./booklink-frontend

echo
echo "==> Done. The k3s node pulls :$TAG from ECR automatically."
echo "   To force a refresh on an already-running cluster:"
echo "   kubectl -n booklink rollout restart deployment"
