# 学習時間記録アプリ AWS構成改善・Terraform化

## 概要

本リポジトリは、開発研修で作成した学習時間記録アプリを題材に、AWS上でのデプロイ構成を再現・改善し、最終的に主要なAWSリソースをTerraformで管理したものです。

研修当時はSpring Bootアプリの実装が主目的だったため、AWS上でどのようにアプリが動作しているかについては、理解が曖昧な部分が残っていました。
そのため、まずは研修時のAWSデプロイ構成を再現し、EC2 / Docker / Nginx / RDS などの役割を整理することから始めました。

その後、アプリをAWS上で運用する中で、画像保存・画像配信・アプリ実行環境・ログ管理などを改善できる余地があると考え、S3、CloudFront、ECR、ECS Fargate、CloudWatch Logsなどを段階的に導入しました。

最後に、手動で構築していたAWS構成をもとに、主要なインフラリソースをTerraformで再作成し、インフラ構成をコードで管理できるようにしました。

本プロジェクトでは、最初から理想的なAWS構成を作るのではなく、研修時の構成を理解し直したうえで、目的に応じて段階的に構成を改善することを重視しています。

---

## 構成の変遷

本プロジェクトでは、AWS構成を以下の3段階で整理しています。

1. 最小構成の再現・整理
   研修時のAWSデプロイ構成を再現し、EC2 / Docker / Nginx / RDS などの役割を整理した構成

2. AWSサービス導入による改修構成
   画像保存、画像配信、コンテナ実行環境、ログ管理などを改善するため、AWSサービスを段階的に導入した構成

3. TerraformによるIaC構成
   手動で構築したAWS構成をもとに、主要なリソースをTerraformで管理した構成

---

## 1. 最小構成の再現・整理

### 目的

研修時にデプロイしていた学習時間記録アプリのAWS構成を再現し、EC2 / Docker / Nginx / RDS を使った基本的なデプロイ構成を理解し直すことを目的としました。

研修当時はSpring Bootアプリの実装が主目的だったため、AWS上でアプリがどのように動作しているか、各リソースがどの役割を担っているかについては、整理しきれていない部分がありました。

そのため、まずは当時の構成をベースに、アプリケーション実行環境、リバースプロキシ、データベース、画像保存先の役割を確認しました。

### 主な構成

* Amazon EC2
* Docker / Docker Compose
* Nginx
* Amazon RDS for PostgreSQL
* EC2上のローカル領域に画像ファイルを保存

この構成では、EC2上でアプリケーションコンテナを起動し、Nginxをリバースプロキシとして利用しました。
データベースにはRDS PostgreSQLを利用し、画像ファイルはEC2上のローカル領域に保存していました。

---

## 2. AWSサービス導入による改修構成

### 目的

最小構成でアプリを動作させた後、AWSサービスを活用して構成を改善しました。

特に、EC2上にアプリケーション実行環境と画像ファイル保存領域が同居している点や、コンテナ実行基盤・ログ管理の面で改善余地があると考えました。

そのため、画像保存、画像配信、Dockerイメージ管理、アプリケーション実行環境、ログ管理をそれぞれAWSサービスへ切り出す形で改修しました。

### 主な変更点

* 画像保存をEC2ローカルからAmazon S3へ移行
* 画像配信をCloudFront + OAC経由に変更
* DockerイメージをAmazon ECRで管理
* アプリケーション実行環境をEC2からECS Fargateへ移行
* ECSコンテナログをCloudWatch Logsへ出力

### 改修後の主な構成

* Amazon S3
* Amazon CloudFront + OAC
* Amazon ECR
* Amazon ECS Fargate
* Amazon RDS for PostgreSQL
* Amazon CloudWatch Logs

この改修により、アプリケーションの実行環境と画像ファイルの保存先を分離しました。
また、S3を直接公開するのではなく、CloudFront + OACを経由して画像を配信する構成としました。

ECS Fargateへの移行では、EC2上で直接コンテナを運用する構成から、AWSのコンテナ実行基盤上でアプリケーションを起動する構成へ変更しました。

---

## 3. TerraformによるIaC構成

### 目的

手動で構築したAWS構成をもとに、主要なインフラリソースをTerraformで管理することを目的としました。

手動構築では、どのリソースをどの設定で作成したかが分かりづらくなりやすいため、Terraformを使って構成をコードとして残し、再現性や差分確認をしやすくすることを目指しました。

今回は、改修構成で学んだ内容を踏まえつつ、ECS Fargateでアプリケーションを起動するために必要な主要リソースを中心にTerraform化しました。

### Terraform化した範囲

* VPC
* Subnet
* Route Table
* Security Group
* Amazon RDS for PostgreSQL
* Amazon ECR
* Amazon ECS Cluster
* Amazon ECS Task Definition
* Amazon ECS Service
* CloudWatch Logs用Log Group

Terraform化では、`terraform plan` による差分確認と、`terraform apply` によるリソース作成を行いながら、AWSリソースを段階的にコード化しました。

なお、今回は学習目的のため、すべてのAWSリソースをTerraform管理対象に含めるのではなく、ECS Fargateでアプリケーションを起動するために必要な主要リソースを中心に管理対象としました。

---

## 学習目的として簡略化した点

本プロジェクトは学習目的のため、本番運用を想定した構成と比較すると、いくつか簡略化している点があります。

* ALBは導入せず、ECS TaskにPublic IPを付与して直接アクセスする構成とした
* ECS ServiceのDesired Countは1とした
* RDSはMulti-AZ構成にしていない
* RDSの削除保護や最終スナップショット取得は有効化していない
* DB接続情報の管理にSecrets Managerは利用していない
* Terraform stateはS3 backendではなくローカル管理としている
* S3 / CloudFrontは今回のTerraform管理対象には含めていない
* dev / prodなどの環境分離は行っていない

これらは本番運用では改善が必要な点ですが、今回はTerraformの学習範囲を広げすぎず、ECS Fargateでアプリケーションを起動し、主要なAWSリソースをTerraformで管理することを優先しました。

---

## 今後の改善予定

今後は、より本番運用に近い構成を目指して、以下の改善を検討しています。

* ALBを導入し、ECS Taskへの直接アクセスを避ける
* ECS TaskをPrivate Subnetに配置する
* Secrets Managerを利用してDB接続情報を管理する
* Terraform stateをS3 backendで管理する
* dev / prodなどの環境分離を行う
* S3 / CloudFrontもTerraform管理対象に含める
* CI/CDを導入し、ECRへのイメージpushからECSデプロイまでを自動化する
* CloudWatch Alarmなどを利用して監視・通知を強化する
