# アプリケーション用の Amazon ECR リポジトリ。
# ビルドしたコンテナイメージを格納し、ECS や CI から pull してデプロイする前提のレジストリ。
resource "aws_ecr_repository" "app" {
  # リポジトリ名。
  # `var.project_name` を接頭辞にし、`/app` でアプリ用イメージと分けて識別しやすくする。
  name = "${var.project_name}/app"

  # 同一タグへの上書き push を許可するか。
  # MUTABLE にすると `latest` などのタグ運用がしやすい。改ざん検知を優先する場合は IMMUTABLE も検討する。
  image_tag_mutability = "MUTABLE"

  # イメージの脆弱性スキャン設定。
  image_scanning_configuration {
    # push 時にスキャンを走らせ、CVE の有無を早期に把握しやすくする。
    scan_on_push = true
  }

  tags = {
    # AWS コンソール上で識別しやすい Name タグ。
    Name = "${var.project_name}-app-ecr"
  }
}
