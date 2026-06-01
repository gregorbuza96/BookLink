output "public_ip" {
  description = "Stable public IP (Elastic IP) of the k3s node."
  value       = aws_eip.node.public_ip
}

output "app_url" {
  description = "Frontend URL (no domain needed)."
  value       = "http://${aws_eip.node.public_ip}/"
}

output "api_url" {
  description = "API base URL through the gateway."
  value       = "http://${aws_eip.node.public_ip}/api"
}

output "ssh_command" {
  description = "SSH into the node (requires key_name set)."
  value       = var.key_name != "" ? "ssh ec2-user@${aws_eip.node.public_ip}" : "set var.key_name to enable SSH"
}

output "fetch_kubeconfig" {
  description = "Pull the cluster kubeconfig to your laptop (requires SSH + key)."
  value       = "ssh ec2-user@${aws_eip.node.public_ip} sudo cat /etc/rancher/k3s/k3s.yaml | sed 's/127.0.0.1/${aws_eip.node.public_ip}/' > booklink.kubeconfig"
}

output "ecr_registry" {
  description = "ECR registry host. Use as ECR_REGISTRY in CI."
  value       = local.ecr_registry
}

output "ecr_repository_urls" {
  description = "Pushable ECR repo URL per service."
  value       = { for k, r in aws_ecr_repository.service : k => r.repository_url }
}
