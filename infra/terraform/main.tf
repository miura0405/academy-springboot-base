# Terraform / AWS プロバイダの設定エントリ

terraform {
  # このコードが想定する Terraform の最低バージョン
  required_version = ">= 1.6.0"

  # 使用するプロバイダとバージョン制約
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

# AWS API の接続先リージョン（variables.tf の aws_region を参照）
provider "aws" {
  region = var.aws_region
}
