# apply 後に表示する値（他モジュールや手作業設定で参照しやすくする）

# 作成した VPC の ID
output "vpc_id" {
  description = "ID of the created VPC"
  value       = aws_vpc.main.id
}

# パブリックサブネットの ID（EC2 などを置く際に指定）
output "public_subnet_id" {
  description = "ID of the created public subnet"
  value       = aws_subnet.public_1a.id
}

# インターネットゲートウェイの ID
output "internet_gateway_id" {
  description = "ID of the created internet gateway"
  value       = aws_internet_gateway.main.id
}

# パブリック用ルートテーブルの ID
output "public_route_table_id" {
  description = "ID of the created public route table"
  value       = aws_route_table.public.id
}
