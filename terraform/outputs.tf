output "region" {
  description = "AWS region."
  value       = var.region
}

output "cluster_name" {
  description = "EKS cluster name."
  value       = module.eks.cluster_name
}

output "configure_kubectl" {
  description = "Run this to point kubectl at the cluster."
  value       = "aws eks update-kubeconfig --region ${var.region} --name ${module.eks.cluster_name}"
}

output "ecr_registry" {
  description = "ECR registry host (set as AWS_ACCOUNT_ID-derived ECR_REGISTRY in CI)."
  value       = "${data.aws_caller_identity.current.account_id}.dkr.ecr.${var.region}.amazonaws.com"
}

output "ecr_repository_urls" {
  description = "Pushable ECR repo URLs per service."
  value       = { for k, r in aws_ecr_repository.service : k => r.repository_url }
}

output "rds_endpoint" {
  description = "RDS endpoint — use as DB_HOST for every service."
  value       = aws_db_instance.postgres.address
}

output "vpc_id" {
  description = "VPC id."
  value       = module.vpc.vpc_id
}

data "aws_caller_identity" "current" {}
