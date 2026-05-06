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
