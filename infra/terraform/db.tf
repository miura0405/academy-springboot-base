# DB Subnet Group。
# RDS インスタンスを「どの Subnet 群に配置してよいか」を定義するためのリソース。
resource "aws_db_subnet_group" "main" {
  # DB Subnet Group の名前。
  # project_name を含めることで、複数環境でも識別しやすくする。
  name = "${var.project_name}-db-subnet-group"

  # RDS が利用可能な Subnet の ID 一覧。
  # ここで指定した Subnet の中から DB が配置されるため、
  # インターネット非公開にしたい場合は Private Subnet を指定する。
  subnet_ids = [
    aws_subnet.private_1a.id,
    aws_subnet.private_1c.id
  ]

  tags = {
    # AWS コンソールで識別しやすくする Name タグ。
    Name = "${var.project_name}-db-subnet-group"
  }
}

resource "aws_db_instance" "main" {
  identifier = "${var.project_name}-postgres"

  engine         = "postgres"
  engine_version = "17.6"
  instance_class = "db.t4g.micro"

  allocated_storage = 20
  storage_type      = "gp3"

  db_name  = var.db_name
  username = var.db_username
  password = var.db_password

  db_subnet_group_name   = aws_db_subnet_group.main.name
  vpc_security_group_ids = [aws_security_group.rds.id]

  publicly_accessible = false
  multi_az            = false

  deletion_protection = false
  skip_final_snapshot = true

  tags = {
    Name = "${var.project_name}-postgres"
  }
}