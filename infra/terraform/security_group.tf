# Security Group は VPC 内リソースの仮想ファイアウォール。
# このファイルでは ECS タスク用と RDS 用を分けて作成し、
# RDS には ECS からだけ接続できるようにする。

# ECS タスク用の Security Group。
# アプリケーションコンテナが受ける通信と、コンテナから外へ出る通信を制御する。
resource "aws_security_group" "ecs" {
  # Security Group 名。AWS アカウント内で識別しやすいよう project_name を含める。
  name = "${var.project_name}-ecs-sg"

  # AWS コンソールや API 上で表示される説明文。
  description = "Security group for ECS tasks"

  # この Security Group を所属させる VPC。
  # 参照元/参照先の Security Group や Subnet は同じ VPC 内で扱う。
  vpc_id = aws_vpc.main.id

  tags = {
    Name = "${var.project_name}-ecs-sg"
  }
}

# ECS タスクへの HTTP アクセス許可。
# 学習用の一時設定として 0.0.0.0/0 から 8080 番を許可しているため、外部から直接到達可能な設定。
# ECSサービスを実際に起動する段階では、自分のIP/32 などに制限する。
resource "aws_vpc_security_group_ingress_rule" "ecs_http_8080" {
  # ルールを追加する ECS 用 Security Group。
  security_group_id = aws_security_group.ecs.id

  # TCP の 8080 番だけを許可する。
  # Spring Boot アプリケーションの待ち受けポートとして使う想定。
  ip_protocol = "tcp"
  from_port   = 8080
  to_port     = 8080

  # 自分の IPv4 アドレスからのアクセスだけを許可する。
  # ALBなしでECSタスクへ直接アクセスする学習用構成のため、
  # 0.0.0.0/0 ではなく検証元IPに絞る。
  cidr_ipv4   = var.my_ip_cidr
  description = "Allow HTTP access to ECS tasks on port 8080"
}

# ECS タスクから外向き通信を許可。
# Docker イメージ取得、外部 API 呼び出し、RDS への接続など、タスク側から開始する通信に必要。
resource "aws_vpc_security_group_egress_rule" "ecs_all_outbound" {
  # ルールを追加する ECS 用 Security Group。
  security_group_id = aws_security_group.ecs.id

  # -1 は全プロトコルを意味する。
  # from_port/to_port を省略して、すべてのポートを対象にしている。
  ip_protocol = "-1"

  # 任意の IPv4 宛先への通信を許可する。
  cidr_ipv4 = "0.0.0.0/0"

  description = "Allow all outbound traffic from ECS tasks"
}

# RDS 用の Security Group。
# PostgreSQL DB インスタンスに適用し、ECS タスクからの DB 接続だけを受ける想定。
resource "aws_security_group" "rds" {
  # RDS 用であることが分かる名前にする。
  name = "${var.project_name}-rds-sg"

  # AWS コンソールや API 上で表示される説明文。
  description = "Security group for RDS PostgreSQL"

  # DB を配置する VPC と同じ VPC に作成する。
  vpc_id = aws_vpc.main.id

  tags = {
    Name = "${var.project_name}-rds-sg"
  }
}

# ECS 用 Security Group から RDS PostgreSQL への接続を許可。
# CIDR ではなく Security Group 参照で許可するため、ECS タスクの IP が変わっても許可範囲を保てる。
resource "aws_vpc_security_group_ingress_rule" "rds_postgresql_from_ecs" {
  # ルールを追加する RDS 用 Security Group。
  security_group_id = aws_security_group.rds.id

  # PostgreSQL の標準ポート 5432/TCP を許可する。
  ip_protocol = "tcp"
  from_port   = 5432
  to_port     = 5432

  # 接続元を ECS 用 Security Group に限定する。
  # これにより、同じ VPC 内でも ECS SG を持たないリソースからの DB 接続は拒否される。
  referenced_security_group_id = aws_security_group.ecs.id
  description                  = "Allow PostgreSQL access from ECS tasks"
}

