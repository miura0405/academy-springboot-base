# 復旧作業ログ

## 2026-03-13

### 変更履歴
1. `Dockerfile` をマルチステージビルドに変更  
Docker イメージ作成時に `./gradlew bootJar --no-daemon` を実行し、生成された jar を実行用イメージにコピーする構成へ変更。

2. `docker-compose.yaml` に永続化ボリュームを追加  
`/var/app/uploads:/app/uploads` を追加し、アップロード画像がコンテナ再作成で消えないように変更。
