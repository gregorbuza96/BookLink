# BookLink — AWS Infrastructure (Terraform)

Provisions everything the EKS deployment needs:

| File | Resources |
|---|---|
| `main.tf` | VPC (2 AZs, single NAT), EKS cluster `booklink-eks` + managed node group |
| `rds.tf` | RDS PostgreSQL instance + subnet group + security group |
| `ecr.tf` | One ECR repo per service (`booklink/<service>`) + lifecycle policy |
| `addons.tf` | Helm: ingress-nginx + cert-manager |
| `clusterissuer.yaml` | Let's Encrypt `letsencrypt-prod` ClusterIssuer (apply manually) |

> ⚠️ Everything marked **PLACEHOLDER** must be replaced before a real deploy:
> `db_password`, `acme_email`, the ingress domain in `k8s/api-gateway/ingress.yaml`,
> and the remote-state backend in `versions.tf`.

## Prerequisites
- Terraform >= 1.5, AWS CLI, `kubectl`, `helm`
- AWS credentials with admin-ish permissions (`aws configure`)

## Usage

```sh
cd terraform
cp terraform.tfvars.example terraform.tfvars   # edit it
export TF_VAR_db_password='choose-a-strong-password'

terraform init
terraform plan
terraform apply
```

If the first apply fails authenticating to the cluster (kube/helm providers
run before the cluster exists), set `install_cluster_addons = false`, apply,
then set it back to `true` and apply again.

## After apply

```sh
# 1. Point kubectl at the cluster
$(terraform output -raw configure_kubectl)

# 2. Create the other two databases (RDS only made booklink_users)
#    Connect via a psql pod or bastion using the rds_endpoint output:
#    CREATE DATABASE booklink_hotels;
#    CREATE DATABASE booklink_bookings;

# 3. Apply the Let's Encrypt issuer (after cert-manager is up)
kubectl apply -f clusterissuer.yaml
```

## Wiring Terraform outputs into the app

After `terraform apply`, update the Kubernetes manifests / CI before deploying:

- **`DB_HOST`** in every `k8s/<service>/deployment.yaml` → `terraform output rds_endpoint`
- **Image names** — CI pushes to `…/booklink/<service>`, but the manifests
  currently reference `…/<service>` (no `booklink/` prefix). Fix that mismatch
  or the pods will hit `ImagePullBackOff`.
- **GitHub Actions secrets**: `AWS_ACCOUNT_ID`, `AWS_ACCESS_KEY_ID`,
  `AWS_SECRET_ACCESS_KEY`.
- **Ingress host** in `k8s/api-gateway/ingress.yaml` → a real domain with DNS
  pointing at the ingress-nginx load balancer.

## Teardown

```sh
terraform destroy
```

Costs accrue per hour while running — destroy when you're done testing.
See the cost estimate in the project chat / main README.
