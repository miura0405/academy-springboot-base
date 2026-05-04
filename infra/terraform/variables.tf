# Terraform 入力変数（リージョン・命名・ネットワークの既定値）

# リソースを作成する AWS リージョン
variable "aws_region" {
  description = "AWS region to create resources in"
  type        = string
  default     = "ap-northeast-1"
}

# リソース名の接頭辞などに使うプロジェクト識別子
variable "project_name" {
  description = "Project name used for resource naming"
  type        = string
  default     = "academy-tf"
}

# VPC の IPv4 アドレス空間
variable "vpc_cidr" {
  description = "CIDR block for the VPC"
  type        = string
  default     = "10.10.0.0/16"
}

# パブリックサブネットの CIDR（VPC 内の一部レンジ）
variable "public_subnet_cidr" {
  description = "CIDR block for the public subnet"
  type        = string
  default     = "10.10.1.0/24"
}

# パブリックサブネットを置くアベイラビリティゾーン
variable "availability_zone" {
  description = "Availability Zone for the public subnet"
  type        = string
  default     = "ap-northeast-1a"
}
