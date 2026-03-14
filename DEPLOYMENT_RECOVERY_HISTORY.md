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
