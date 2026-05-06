# Terraform 入力変数。
# ここにまとめておくことで、環境差分や命名規則をコード本体から切り離して変更しやすくする。
# 値を上書きしたい場合は terraform.tfvars や -var オプションを使う。

# リソースを作成する AWS リージョン。
# Provider の region に渡され、aws_* リソースの作成先リージョンを決める。
variable "aws_region" {
  description = "AWS region to create resources in"
  type        = string
  default     = "ap-northeast-1"
}

# リソース名の接頭辞などに使うプロジェクト識別子。
# Name タグや Security Group 名に含めることで、AWS コンソール上で関連リソースを探しやすくする。
variable "project_name" {
  description = "Project name used for resource naming"
  type        = string
  default     = "academy-tf"
}

# VPC の IPv4 アドレス空間。
# 各 Subnet の CIDR は、この範囲に収まるように設計する必要がある。
variable "vpc_cidr" {
  description = "CIDR block for the VPC"
  type        = string
  default     = "10.10.0.0/16"
}

# 1a 側 Public Subnet の CIDR。
# Internet Gateway へのルートを持たせる Subnet なので、外部公開リソースの配置先になる。
variable "public_subnet_cidr" {
  description = "CIDR block for the public subnet"
  type        = string
  default     = "10.10.1.0/24"
}

# 1a 側 Subnet を置く Availability Zone。
# public_1a と private_1a の両方で使い、同じ AZ 内に公開/非公開の Subnet を用意する。
variable "availability_zone" {
  description = "Availability Zone for the public subnet"
  type        = string
  default     = "ap-northeast-1a"
}

# 1c 側 Public Subnet の CIDR。
# public_subnet_cidr と重複しない範囲にして、複数 AZ の公開 Subnet を構成する。
variable "public_subnet_1c_cidr" {
  description = "CIDR block for the public subnet in ap-northeast-1c"
  type        = string
  default     = "10.10.2.0/24"
}

# 1a 側 Private Subnet の CIDR。
# RDS や内部向け ECS タスクなど、インターネットから直接到達させないリソースの配置先を想定する。
variable "private_subnet_1a_cidr" {
  description = "CIDR block for the private subnet in ap-northeast-1a"
  type        = string
  default     = "10.10.11.0/24"
}

# 1c 側 Private Subnet の CIDR。
# DB Subnet Group やアプリケーションの冗長構成で、別 AZ の非公開配置先として使える。
variable "private_subnet_1c_cidr" {
  description = "CIDR block for the private subnet in ap-northeast-1c"
  type        = string
  default     = "10.10.12.0/24"
}

# 1c 側 Subnet を置く Availability Zone。
# public_1c と private_1c で共有し、ap-northeast-1a とは別 AZ にリソースを分散する。
variable "availability_zone_1c" {
  description = "Availability Zone for the second subnet"
  type        = string
  default     = "ap-northeast-1c"
}
