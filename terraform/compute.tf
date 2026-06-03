# Latest Amazon Linux 2023 ARM64 image (matches t4g / Graviton)
data "aws_ami" "al2023" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["al2023-ami-2023.*-arm64"]
  }
  filter {
    name   = "architecture"
    values = ["arm64"]
  }
}

data "aws_caller_identity" "current" {}

locals {
  ecr_registry = "${data.aws_caller_identity.current.account_id}.dkr.ecr.${var.region}.amazonaws.com"

  # Rendered Kubernetes manifests for the whole stack (namespace, secrets,
  # Postgres, Redis, the 5 services + frontend, and the Traefik ingress).
  manifests = templatefile("${path.module}/templates/manifests.yaml.tftpl", {
    registry    = local.ecr_registry
    db_password = var.db_password
    jwt_secret  = var.jwt_secret
    # Gateway CORS allowCredentials=true forbids "*"; use the node's own origin.
    allowed_origins = "http://${aws_eip.node.public_ip}"
  })

  user_data = templatefile("${path.module}/templates/userdata.sh.tftpl", {
    region       = var.region
    ecr_registry = local.ecr_registry
    manifests    = local.manifests
  })
}

# Stable public IP that survives spot stop/start (no domain needed).
resource "aws_eip" "node" {
  domain = "vpc"
  tags   = merge(local.tags, { Name = "${local.name}-eip" })
}

resource "aws_instance" "node" {
  ami                    = data.aws_ami.al2023.id
  instance_type          = var.instance_type
  subnet_id              = aws_subnet.public.id
  vpc_security_group_ids = [aws_security_group.node.id]
  iam_instance_profile   = aws_iam_instance_profile.node.name
  key_name               = var.key_name != "" ? var.key_name : null
  user_data              = local.user_data

  root_block_device {
    volume_size = var.root_volume_gb
    volume_type = "gp3"
    encrypted   = true
  }

  # Cheapest option: persistent spot that STOPS (not terminates) on
  # interruption, so the root volume + Postgres data survive and AWS
  # brings it back automatically when capacity returns.
  dynamic "instance_market_options" {
    for_each = var.capacity_type == "spot" ? [1] : []
    content {
      market_type = "spot"
      spot_options {
        spot_instance_type             = "persistent"
        instance_interruption_behavior = "stop"
      }
    }
  }

  tags = merge(local.tags, { Name = "${local.name}-k3s" })
}

resource "aws_eip_association" "node" {
  instance_id   = aws_instance.node.id
  allocation_id = aws_eip.node.id
}
