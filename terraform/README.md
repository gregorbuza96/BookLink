# BookLink — cheapest AWS Kubernetes (k3s on one EC2)

Runs the whole stack on a **single small EC2** with **k3s** (full, CNCF-conformant
Kubernetes). No EKS control-plane fee, no NAT gateway, no managed load balancer —
the cheapest way to have real Kubernetes on AWS.

## What it provisions

| File | Resources |
|---|---|
| `network.tf` | VPC, one **public** subnet, Internet Gateway, security group |
| `iam.tf` | Instance role with ECR read-only (no AWS keys on the box) |
| `compute.tf` | EC2 (`t4g.medium`, spot by default) + k3s bootstrap + **Elastic IP** |
| `ecr.tf` | One ECR repo per service (`booklink/<service>`) |
| `templates/userdata.sh.tftpl` | Installs k3s, ECR auth refresh, deploys the stack |
| `templates/manifests.yaml.tftpl` | Namespace, Postgres, Redis, 5 services + frontend, Traefik ingress |

The node runs: **config-server, api-gateway, user-service, hotel-service,
booking-service, frontend, Postgres, Redis** — each capped to fit 4 GB RAM.
Monitoring (Prometheus/Grafana) is omitted to fit the smallest box; add it back
on a larger instance.

## Why it's internet-accessible (no domain)

1. **Elastic IP** — a stable public IP (survives spot stop/start).
2. **Security group** — port **80/443 open to the world**; SSH (22) + k3s API
   (6443) restricted to `admin_cidr`.
3. **Traefik ingress** (built into k3s) listens on port 80 of the node and routes
   `/` → frontend and `/api` → api-gateway.

Result: the app is reachable at **`http://<elastic-ip>/`** with no DNS at all.
HTTPS is skipped because Let's Encrypt needs a domain — add a domain later if you
want TLS.

## Usage

```sh
cd terraform
cp terraform.tfvars.example terraform.tfvars   # edit admin_cidr, key_name
export TF_VAR_db_password='choose-a-strong-password'
export TF_VAR_jwt_secret='a-long-random-secret'

terraform init
terraform apply
```

Then **push the images** (the pods pull `:latest` from ECR). Easiest is the
existing GitHub Actions pipeline, or locally:

```sh
REGISTRY=$(terraform output -raw ecr_registry)
aws ecr get-login-password --region eu-central-1 | docker login --username AWS --password-stdin "$REGISTRY"
mvn package -DskipTests
for s in config-server api-gateway user-service hotel-service booking-service; do
  docker build -t "$REGISTRY/booklink/$s:latest" "./$s"
  docker push "$REGISTRY/booklink/$s:latest"
done
docker build -t "$REGISTRY/booklink/frontend:latest" ./booklink-frontend
docker push "$REGISTRY/booklink/frontend:latest"
```

Within a couple of minutes the pods pull the images and start. Open:

```sh
terraform output app_url
```

> First boot takes a few minutes (k3s install + image pulls + Spring Boot
> startup). Check progress over SSH: `sudo tail -f /var/log/booklink-bootstrap.log`
> and `sudo k3s kubectl get pods -n booklink`.

## Cost (eu-central-1)

| Item | Spot | On-demand |
|---|---|---|
| t4g.medium (4 GB) | ~$8–10/mo | ~$26/mo |
| 30 GB gp3 EBS | ~$2.4/mo | ~$2.4/mo |
| Elastic IP (attached) | free | free |
| Data transfer (dev) | ~free | ~free |
| **Total** | **~$10–13/mo (~$0.40/day)** | **~$29/mo (~$0.95/day)** |

For a few days of testing on spot: **~$1–2 total**. Run `terraform destroy` when done.

## Notes & limits
- **Single node** = no high availability. If the box dies, the app is down until
  it restarts. Fine for dev/test.
- **Spot** can be interrupted; with `instance_interruption_behavior = stop` the
  instance stops and AWS restarts it automatically when capacity returns — the
  EBS volume (incl. Postgres data) is preserved. Use `capacity_type = "on-demand"`
  to avoid interruptions entirely.
- **Data** lives on the node's EBS volume. Back it up if it matters.
- Not validated locally (no Terraform installed here) — review `terraform plan`
  before applying.

## Teardown

```sh
terraform destroy
```
