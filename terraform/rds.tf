# ── RDS PostgreSQL ────────────────────────────────────────
# A single Postgres instance hosting all three logical databases
# (booklink_users, booklink_hotels, booklink_bookings) to keep cost down.
#
# NOTE: aws_db_instance only creates ONE initial database. After apply you
# must create the other two (see terraform/README.md → "Create databases").
# Point every service's DB_HOST env var at aws_db_instance.postgres.address.

resource "aws_db_subnet_group" "this" {
  name       = "${local.name}-db"
  subnet_ids = module.vpc.private_subnets
  tags       = local.tags
}

resource "aws_security_group" "rds" {
  name        = "${local.name}-rds"
  description = "Allow Postgres access from EKS nodes only"
  vpc_id      = module.vpc.vpc_id

  ingress {
    description     = "Postgres from EKS worker nodes"
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    security_groups = [module.eks.node_security_group_id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = local.tags
}

resource "aws_db_instance" "postgres" {
  identifier     = "${local.name}-pg"
  engine         = "postgres"
  engine_version = var.db_engine_version
  instance_class = var.db_instance_class

  allocated_storage = var.db_allocated_storage
  storage_type      = "gp3"
  storage_encrypted = true

  db_name  = "booklink_users" # initial DB; create the other two post-apply
  username = var.db_username
  password = var.db_password

  db_subnet_group_name   = aws_db_subnet_group.this.name
  vpc_security_group_ids = [aws_security_group.rds.id]

  multi_az            = false # single-AZ for dev to save cost
  publicly_accessible = false

  backup_retention_period = 1
  skip_final_snapshot     = true # placeholder: convenient for dev teardown
  deletion_protection     = false

  tags = local.tags
}
