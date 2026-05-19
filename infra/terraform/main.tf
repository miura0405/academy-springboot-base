# Terraform / AWS プロバイダの設定エントリ。
# このファイルでは「Terraform 自体の実行条件」と「AWS へ接続するための Provider」を定義する。
# 実際の VPC・Subnet・Security Group などの AWS リソースは、用途ごとに別ファイルへ分けている。

terraform {
  # このコードが想定する Terraform の最低バージョン。
  # チームや CI 環境で古い Terraform を使って予期しない挙動になることを防ぐ。
  required_version = ">= 1.6.0"

  # 使用するプロバイダとバージョン制約。
  # required_providers に書いた内容をもとに、terraform init 時に Provider Plugin が取得される。
  required_providers {
    aws = {
      # HashiCorp 公式の AWS Provider を利用する。
      source = "hashicorp/aws"

      # 5.x 系の最新互換バージョンを許可する指定。
      # メジャーバージョン 6 には上がらないため、破壊的変更を避けやすい。
      version = "~> 5.0"
    }
  }
}

# AWS API の接続先リージョン。
# var.aws_region は variables.tf で定義しており、既定では東京リージョン ap-northeast-1 を使う。
# この provider ブロックを参照して、以降の aws_* リソースが同じリージョンに作成される。
provider "aws" {
  region = var.aws_region
}
