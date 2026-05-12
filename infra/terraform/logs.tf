# ECS アプリケーション用の CloudWatch Logs ロググループ。
# `awslogs` ドライバーでコンテナ stdout/stderr を送る先として定義し、タスク定義の logConfiguration と整合させる。
resource "aws_cloudwatch_log_group" "ecs_app" {
  # ロググループ名。
  # `/ecs/<project>/app` の慣習に合わせ、同一アカウント内で用途が分かるパスにする。
  name = "/ecs/${var.project_name}/app"

  # ログイベントの保持日数。
  # コストと調査に必要な期間のバランスを取り、学習・検証用途では 14 日程度を想定している。
  retention_in_days = 14

  tags = {
    # AWS コンソール上で識別しやすい Name タグ。
    Name = "${var.project_name}-ecs-app-logs"
  }
}
