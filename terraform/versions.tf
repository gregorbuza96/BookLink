terraform {
  required_version = ">= 1.5.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.40"
    }
  }

  # Remote state is optional for a single-node dev box; local state is fine.
  # backend "s3" { ... }
}

provider "aws" {
  region = var.region

  default_tags {
    tags = local.tags
  }
}
