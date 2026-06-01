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
  description = "Environment name."
  type        = string
  default     = "dev"
}

# ── Networking / access ───────────────────────────────────
variable "vpc_cidr" {
  description = "CIDR block for the VPC."
  type        = string
  default     = "10.0.0.0/16"
}

variable "admin_cidr" {
  description = "Your IP (CIDR) allowed to reach SSH (22) and the k3s API (6443). PLACEHOLDER — set to e.g. \"203.0.113.10/32\". Default 0.0.0.0/0 is open to the world; tighten it."
  type        = string
  default     = "0.0.0.0/0"
}

variable "key_name" {
  description = "Name of an EXISTING EC2 key pair for SSH access. Leave \"\" to launch without SSH access."
  type        = string
  default     = ""
}

# ── Compute (smallest box that fits the whole stack) ──────
variable "instance_type" {
  description = "EC2 instance type. t4g.medium (4GB, ARM) is the smallest that reliably runs all 5 Java services + Postgres + Redis. t4g.small (2GB) will OOM."
  type        = string
  default     = "t4g.medium"
}

variable "capacity_type" {
  description = "\"spot\" (cheapest, auto-stops/restarts on interruption — data persists) or \"on-demand\" (no interruptions, ~3x the price)."
  type        = string
  default     = "spot"

  validation {
    condition     = contains(["spot", "on-demand"], var.capacity_type)
    error_message = "capacity_type must be \"spot\" or \"on-demand\"."
  }
}

variable "root_volume_gb" {
  description = "Root EBS volume size (GB). Holds the OS, k3s, container images and Postgres data."
  type        = number
  default     = 30
}

# ── App secrets ───────────────────────────────────────────
variable "db_password" {
  description = "Postgres password (in-cluster). PLACEHOLDER — override via TF_VAR_db_password."
  type        = string
  default     = "CHANGE_ME_placeholder_password"
  sensitive   = true
}

variable "jwt_secret" {
  description = "JWT signing secret shared by the services. PLACEHOLDER — override via TF_VAR_jwt_secret."
  type        = string
  default     = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970"
  sensitive   = true
}

variable "services" {
  description = "Microservices that get an ECR repo (matches CI push paths booklink/<service>)."
  type        = list(string)
  default = [
    "config-server",
    "api-gateway",
    "user-service",
    "hotel-service",
    "booking-service",
    "frontend",
  ]
}
