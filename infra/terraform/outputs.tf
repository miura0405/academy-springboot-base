# terraform apply 後に表示する出力値。
# 作成されたリソース ID を確認したり、後続の手作業設定・別モジュール連携で参照したりするために使う。

# 作成した VPC の ID。
# Subnet、Security Group、RDS などを追加するときの親ネットワークとして参照できる。
output "vpc_id" {
  description = "ID of the created VPC"
  value       = aws_vpc.main.id
}

# 1a 側 Public Subnet の ID。
# EC2、ECS タスク、ALB などをこの Subnet に配置するときに利用できる。
output "public_subnet_id" {
  description = "ID of the created public subnet"
  value       = aws_subnet.public_1a.id
}

# Internet Gateway の ID。
# VPC の外部接続口として作成されていることを確認するための出力。
output "internet_gateway_id" {
  description = "ID of the created internet gateway"
  value       = aws_internet_gateway.main.id
}

# Public Route Table の ID。
# Public Subnet を追加する場合、この Route Table に関連付けることで同じインターネット向け経路を使える。
output "public_route_table_id" {
  description = "ID of the created public route table"
  value       = aws_route_table.public.id
}

# アプリケーション用 ECR リポジトリのレジストリ URL。
# `docker push` の接続先や、ECS タスク定義の `image` に指定する URI のベースとして使える。
output "ecr_repository_url" {
  description = "URL of the ECR repository for the application"
  value       = aws_ecr_repository.app.repository_url
}

# 作成した ECS クラスターの名前。
# `aws ecs` CLI、GitHub Actions、デプロイスクリプトで `--cluster` に渡すときに使える。
output "ecs_cluster_name" {
  description = "Name of the created ECS cluster"
  value       = aws_ecs_cluster.main.name
}

# ECS クラスターの ARN。
# IAM ポリシーの Condition、サービスリンクロール関連、イベント連携などで ARN を要求される場合に参照する。
output "ecs_cluster_arn" {
  description = "ARN of the created ECS cluster"
  value       = aws_ecs_cluster.main.arn
} 