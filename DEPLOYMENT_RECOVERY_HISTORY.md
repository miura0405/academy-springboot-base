# 復旧作業ログ

## 2026-03-13

### 変更履歴
1. `Dockerfile` をマルチステージビルドに変更  
Docker イメージ作成時に `./gradlew bootJar --no-daemon` を実行し、生成された jar を実行用イメージにコピーする構成へ変更。

2. `docker-compose.yaml` に永続化ボリュームを追加  
`/var/app/uploads:/app/uploads` を追加し、アップロード画像がコンテナ再作成で消えないように変更。

## 2026-03-14

### 変更履歴
1. `Dockerfile` のビルド手順を見直し  
Gradle 関連ファイルを先にコピーし、`gradlew` に実行権限を付与したうえで `bootJar` を実行する構成に変更。jar のコピーもファイル名固定ではなく `build/libs/*.jar` を参照する形に変更。

2. Docker Compose の設定をローカル用と本番用に分離  
共通設定を `docker-compose.yaml` に残し、ローカル用の `docker-compose.local.yaml` と本番用の `docker-compose.prod.yaml` を追加。

3. 環境変数ファイルを用途別に整理  
`.env.local` をローカル用、`.env.prod` を本番用として扱う構成に変更し、`.gitignore` に追加。

## 2026-03-15

### 変更履歴
1. VPC を作成  
`academy-prod-vpc` を作成。IPv4 CIDR は `10.0.0.0/16`、IPv6 は未使用。

2. サブネットを作成  
以下の 4 つのサブネットを作成。  
- `academy-prod-public-1a` : `10.0.1.0/24` in `ap-northeast-1a`
- `academy-prod-public-1c` : `10.0.2.0/24` in `ap-northeast-1c`
- `academy-prod-private-db-1a` : `10.0.11.0/24` in `ap-northeast-1a`
- `academy-prod-private-db-1c` : `10.0.12.0/24` in `ap-northeast-1c`

3. Public Subnet の自動割り当て設定を変更  
`academy-prod-public-1a` と `academy-prod-public-1c` で、パブリック IPv4 アドレスの自動割り当てを有効化。

4. Internet Gateway と Route Table を作成  
`academy-prod-igw` を作成して `academy-prod-vpc` にアタッチ。  
`academy-prod-public-rt` を作成し、`0.0.0.0/0 -> academy-prod-igw` のルートを追加。  
この Route Table を `academy-prod-public-1a` と `academy-prod-public-1c` に関連付け。

5. EC2 用 Security Group を作成  
`academy-prod-ec2-sg` を作成。  
以下のインバウンドルールを設定。  
- SSH `22` : 自分のグローバル IP `/32`
- HTTP `80` : `0.0.0.0/0`
- HTTPS `443` : `0.0.0.0/0`

6. RDS 用 Security Group を作成  
`academy-prod-rds-sg` を作成。  
PostgreSQL `5432` の受信を `academy-prod-ec2-sg` からのみ許可する設定を追加。

## 2026-03-16

### 変更履歴
1. DB Subnet Group を作成  
`academy-prod-db-subnet-group` を作成。  
`academy-prod-vpc` に紐付け、以下の private DB subnet を追加。  
- `academy-prod-private-db-1a`
- `academy-prod-private-db-1c`

2. RDS for PostgreSQL を作成  
`academy-prod-db` を作成。  
PostgreSQL を選択し、`academy-prod-vpc`、`academy-prod-db-subnet-group`、`academy-prod-rds-sg` を使用する構成で作成。  
パブリックアクセスは無効とし、初期データベース名は `academy_springboot_base` を設定。

## 2026-03-17

### 変更履歴
1. EC2 インスタンスを作成  
`academy-prod-app` を作成。  
パブリックサブネットに配置し、パブリック IP の自動割り当てを有効化。  
セキュリティグループには `academy-prod-ec2-sg` を設定。

2. EC2 へ SSH 接続  
作成したキーペアを使用して EC2 へ SSH 接続を実施。

3. EC2 から RDS への接続確認を実施  
PostgreSQL クライアントを導入し、EC2 から RDS へ `psql` で接続できることを確認。

## 2026-03-18

### 変更履歴
1. EC2 に Docker と Git を導入  
OS パッケージを更新し、Docker と Git をインストール。  
Docker サービスの起動と自動起動設定を行い、`ec2-user` で Docker を実行できるよう権限を設定。

2. アプリ配置用ディレクトリを作成  
以下のディレクトリを作成し、`ec2-user` が操作できるように所有者を変更。  
- `/opt/academy-springboot-base`
- `/var/app/uploads`

3. Git からソースコードを配置  
`/opt` 配下にリポジトリを clone し、アプリのソースコードを配置。

4. `.env.prod` を作成  
EC2 上で `.env.prod` を作成し、Spring Boot の `prod` プロファイル用 DB 接続情報を設定。

5. Docker Compose で build / 起動を実施  
`docker-compose.yaml` と `docker-compose.prod.yaml` を使用して build と起動を実施。  
`docker compose ps` と `docker logs` で状態を確認。

6. EC2 内部からアプリの動作確認を実施  
`curl` により `127.0.0.1:8080` へアクセスし、`/login` へのリダイレクトとログイン画面の HTML を確認。

7. デプロイ方法を見直し  
ローカルで jar を作成して転送する方式から、EC2 上でソースコードを取得して Docker Compose で build / 起動する方式へ変更。

## 2026-03-20

### 変更履歴
1. Nginx を導入  
EC2 に Nginx をインストール。

2. Nginx のリバースプロキシ設定を追加  
`/etc/nginx/conf.d/academy.conf` を作成し、`127.0.0.1:8080` へ転送する設定を追加。

3. Nginx の起動確認を実施  
設定ファイルの構文確認後、Nginx を起動し、自動起動を設定。

4. EC2 内部から Nginx 経由の動作確認を実施  
`127.0.0.1` へアクセスし、`/login` へのリダイレクトとログイン画面の HTML を確認。

5. 公開ポート構成を確認  
外部公開は `80` 番のみとし、`8080` 番は外部公開しない構成で確認。
