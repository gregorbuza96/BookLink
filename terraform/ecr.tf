# One ECR repository per microservice.
# Names match the CI/CD push paths: $ECR_REGISTRY/booklink/<service>
# (see .github/workflows/ci-cd.yml). The pipeline also auto-creates these,
# but managing them here keeps lifecycle policy + scanning consistent.
resource "aws_ecr_repository" "service" {
  for_each = toset(var.services)

  name                 = "${var.project}/${each.value}"
  image_tag_mutability = "MUTABLE"
  force_delete         = true # placeholder: allows `terraform destroy` to remove non-empty repos

  image_scanning_configuration {
    scan_on_push = true
  }

  tags = local.tags
}

# Keep only the most recent images to avoid unbounded storage cost.
resource "aws_ecr_lifecycle_policy" "service" {
  for_each   = aws_ecr_repository.service
  repository = each.value.name

  policy = jsonencode({
    rules = [{
      rulePriority = 1
      description  = "Keep last 10 images"
      selection = {
        tagStatus   = "any"
        countType   = "imageCountMoreThan"
        countNumber = 10
      }
      action = { type = "expire" }
    }]
  })
}
