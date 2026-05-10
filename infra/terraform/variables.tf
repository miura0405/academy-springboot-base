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
# `aws_ecr_repository.app` のリポジトリ名 `${var.project_name}/app` の接頭辞としても使う。
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

# アプリケーションが利用する DB 名（初期データベース名）。
# `aws_db_instance.main` の `db_name` に渡され、RDS 作成時にこの名前の DB が作られる。
# 後から変更すると差分が出やすいため、環境ごとに変える場合は tfvars 側で上書きするのが安全。
variable "db_name" {
  description = "Database name for the application"
  type        = string
  default     = "academy_tf_db"
}

# RDS のマスターユーザー名。
# アプリケーションの接続ユーザーとしても使い回す場合は権限設計に注意し、
# 本番ではアプリ用の権限を絞ったユーザーを別途作る運用も検討する。
variable "db_username" {
  description = "Master username for the RDS database"
  type        = string
  default     = "academy_admin"
}

# RDS のマスターパスワード。
# Terraform state に値が残る点に注意（`sensitive = true` は表示抑制であり、state から消えるわけではない）。
# 実運用では Secrets Manager / SSM Parameter Store 参照に寄せるのが望ましい。
variable "db_password" {
  description = "Master password for the RDS database"
  type        = string
  sensitive   = true
}