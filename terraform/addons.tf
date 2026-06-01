# ── Cluster add-ons (ingress-nginx + cert-manager) ────────
# Required by the existing manifests: k8s/api-gateway/ingress.yaml uses the
# nginx ingress class and the cert-manager "letsencrypt-prod" ClusterIssuer.
#
# If `terraform apply` fails authenticating to the cluster on the very first
# run (chicken-and-egg with the kube provider), set install_cluster_addons
# = false, apply once to build the cluster, then set it back to true and
# apply again.

resource "helm_release" "ingress_nginx" {
  count = var.install_cluster_addons ? 1 : 0

  name             = "ingress-nginx"
  repository       = "https://kubernetes.github.io/ingress-nginx"
  chart            = "ingress-nginx"
  version          = "4.11.2"
  namespace        = "ingress-nginx"
  create_namespace = true

  set {
    name  = "controller.service.type"
    value = "LoadBalancer"
  }

  depends_on = [module.eks]
}

resource "helm_release" "cert_manager" {
  count = var.install_cluster_addons ? 1 : 0

  name             = "cert-manager"
  repository       = "https://charts.jetstack.io"
  chart            = "cert-manager"
  version          = "v1.15.3"
  namespace        = "cert-manager"
  create_namespace = true

  set {
    name  = "crds.enabled"
    value = "true"
  }

  depends_on = [module.eks]
}

# The "letsencrypt-prod" ClusterIssuer referenced by the ingress is applied
# separately so Terraform doesn't need the cert-manager CRDs present at plan
# time. See terraform/clusterissuer.yaml and the README.
