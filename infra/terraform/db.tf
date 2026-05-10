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
  # DB インスタンスの識別子。
  # AWS コンソールや CLI でインスタンス名として表示される。
  identifier = "${var.project_name}-postgres"

  # 利用する DB エンジンの種類とバージョン。
  # 今回は PostgreSQL の 17.6 系を指定している。
  engine         = "postgres"
  engine_version = "17.6"

  # RDS インスタンスのクラス（性能・料金帯）。
  # 学習用途のため、無料枠に近い小さめのインスタンスタイプを選択している。
  instance_class = "db.t4g.micro"

  # DB ストレージの容量と種別。
  # 20 GiB の汎用 SSD(gp3) としており、必要に応じて後から拡張可能。
  allocated_storage = 20
  storage_type      = "gp3"

  # デフォルトで作成する DB 名と、接続ユーザー情報。
  # 本番環境では password を SSM Parameter Store や Secrets Manager から参照する構成が望ましい。
  db_name  = var.db_name
  username = var.db_username
  password = var.db_password

  # どの Subnet Group 配下に DB を配置するか。
  # Private Subnet のみを含んだ Subnet Group を指定することで、RDS をインターネット非公開に保つ。
  db_subnet_group_name = aws_db_subnet_group.main.name

  # 適用する Security Group 一覧。
  # rds 用 SG を指定し、ECS タスクからの PostgreSQL アクセスだけを受けられるようにしている。
  vpc_security_group_ids = [aws_security_group.rds.id]

  # RDS に Public IP を持たせるかどうか。
  # false にすることで、インターネットから直接到達できない Private 側のエンドポイントのみになる。
  publicly_accessible = false

  # Multi-AZ 構成にするかどうか。
  # 学習用のためコストを抑える目的で false にしているが、本番では可用性要件に応じて検討する。
  multi_az = false

  # 誤削除防止とスナップショット取得の挙動。
  # 学習用環境では削除しやすいように deletion_protection を無効にし、
  # skip_final_snapshot を true にして削除時のスナップショット作成を省略している。
  deletion_protection = false
  skip_final_snapshot = true

  tags = {
    # RDS インスタンス自体に付与する Name タグ。
    Name = "${var.project_name}-postgres"
  }
}