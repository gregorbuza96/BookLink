variable "region" {
  description = "AWS region to deploy into."
  type        = string
  default     = "eu-central-1"
}

variable "project" {
  description = "Project name, used as a prefix for resource names."
  type        = string
  default     = "booklink"
}

variable "environment" {
  description = "Environment name (dev, staging, prod)."
  type        = string
  default     = "dev"
}

# ── Networking ────────────────────────────────────────────
variable "vpc_cidr" {
  description = "CIDR block for the VPC."
  type        = string
  default     = "10.0.0.0/16"
}

variable "az_count" {
  description = "Number of Availability Zones to spread subnets across."
  type        = number
  default     = 2
}

# ── EKS ───────────────────────────────────────────────────
variable "kubernetes_version" {
  description = "EKS control-plane Kubernetes version."
  type        = string
  default     = "1.30"
}

variable "node_instance_types" {
  description = "Instance types for the managed node group."
  type        = list(string)
  default     = ["t3.large"] # ~12 pods fit on 2 nodes; t3.medium is cheaper but tight
}

variable "node_desired_size" {
  description = "Desired number of worker nodes."
  type        = number
  default     = 2
}

variable "node_min_size" {
  description = "Minimum number of worker nodes."
  type        = number
  default     = 1
}

variable "node_max_size" {
  description = "Maximum number of worker nodes."
  type        = number
  default     = 3
}

# ── RDS PostgreSQL ────────────────────────────────────────
variable "db_instance_class" {
  description = "RDS instance class."
  type        = string
  default     = "db.t4g.micro" # cheapest burstable; bump to db.t4g.small if needed
}

variable "db_allocated_storage" {
  description = "RDS storage in GB."
  type        = number
  default     = 20
}

variable "db_engine_version" {
  description = "PostgreSQL engine version."
  type        = string
  default     = "16.3"
}

variable "db_username" {
  description = "Master username for RDS."
  type        = string
  default     = "booklink"
}

variable "db_password" {
  description = "Master password for RDS. PLACEHOLDER — override via TF_VAR_db_password or a secrets manager, never commit a real value."
  type        = string
  default     = "CHANGE_ME_placeholder_password"
  sensitive   = true
}

# ── Microservices (used to create one ECR repo per service) ─
variable "services" {
  description = "Service names that get an ECR repository (matches the CI/CD push paths booklink/<service>)."
  type        = list(string)
  default = [
    "config-server",
    "api-gateway",
    "user-service",
    "hotel-service",
    "booking-service",
  ]
}

# ── Add-ons ───────────────────────────────────────────────
variable "install_cluster_addons" {
  description = "Install ingress-nginx + cert-manager via Helm. Set false to apply infra first, add-ons in a second apply."
  type        = bool
  default     = true
}

variable "acme_email" {
  description = "Email for Let's Encrypt / cert-manager ClusterIssuer. PLACEHOLDER."
  type        = string
  default     = "you@example.com"
}
