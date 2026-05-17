# ECS タスク定義（Fargate 向け）。
# ECR のアプリイメージを 1 コンテナで起動し、Spring Boot の prod プロファイルと RDS 接続情報を環境変数で渡す。
# 実行ロールはイメージ pull・CloudWatch Logs 出力、タスクロールはアプリから AWS API を呼ぶ際に利用する。
resource "aws_ecs_task_definition" "app" {
  # タスク定義ファミリー名。
  # リビジョン管理の単位として ECS コンソールや CLI で識別しやすいよう project_name を含める。
  family = "${var.project_name}-app-task"
  # Fargate での起動を前提とする互換性指定。
  requires_compatibilities = ["FARGATE"]
  # awsvpc は ENI 単位で VPC・Security Group を割り当てる Fargate の標準ネットワークモード。
  network_mode = "awsvpc"
  # vCPU とメモリ（Fargate の有効な組み合わせ）。
  # 学習・検証用途の Spring Boot 単体コンテナ向けに 0.5 vCPU / 1 GiB を指定している。
  cpu    = "512"
  memory = "1024"

  # ECS エージェントが ECR pull や awslogs 送信に使うロール。
  execution_role_arn = aws_iam_role.ecs_task_execution_role.arn
  # アプリケーションコンテナが AWS リソースへアクセスする際のロール（現状は最小構成）。
  task_role_arn = aws_iam_role.ecs_task_role.arn

  # コンテナ定義の JSON 配列。
  # 下記は単一コンテナ `app` のイメージ・ポート・環境変数・ログ設定をまとめたもの。
  container_definitions = jsonencode([
    {
      # コンテナ名。サービス定義の container_name と一致させる。
      name = "app"
      # デプロイ対象イメージ。同一リポジトリの latest タグを参照する。
      image     = "${aws_ecr_repository.app.repository_url}:latest"
      essential = true

      # ホスト／コンテナのポートマッピング。
      # Spring Boot の待ち受け 8080 を Fargate タスクの ENI 8080 に公開する。
      portMappings = [
        {
          containerPort = 8080
          hostPort      = 8080
          protocol      = "tcp"
        }
      ]

      # Spring Boot 起動時に参照する環境変数。
      # prod プロファイルと RDS（PostgreSQL）接続先・認証情報を注入する。
      environment = [
        {
          name  = "SPRING_PROFILES_ACTIVE"
          value = "prod"
        },
        {
          name  = "SPRING_DATASOURCE_URL"
          value = "jdbc:postgresql://${aws_db_instance.main.address}:5432/${aws_db_instance.main.db_name}"
        },
        {
          name  = "SPRING_DATASOURCE_USERNAME"
          value = aws_db_instance.main.username
        },
        {
          name  = "SPRING_DATASOURCE_PASSWORD"
          value = var.db_password_parameter_arn
        }
      ]

      # CloudWatch Logs へのログ出力設定。
      # logs.tf で定義したロググループへ awslogs ドライバーで stdout/stderr を送る。
      logConfiguration = {
        logDriver = "awslogs"
        options = {
          awslogs-group         = aws_cloudwatch_log_group.ecs_app.name
          awslogs-region        = var.aws_region
          awslogs-stream-prefix = "ecs"
        }
      }
    }
  ])

  tags = {
    # AWS コンソール上で識別しやすい Name タグ。
    Name = "${var.project_name}-app-task"
  }
}
