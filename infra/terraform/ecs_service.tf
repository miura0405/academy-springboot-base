# ECS サービス（Fargate）。
# 既存クラスター上でタスク定義を常時起動し、指定数のタスクを維持する。ネットワークは awsvpc で Public Subnet に配置し、
# イメージ取得や外向き通信のためにパブリック IP を付与する。ロードバランサーは未接続のシンプル構成。

resource "aws_ecs_service" "app" {
  # サービス名。クラスター内で一意に識別し、デプロイやスケーリングの対象として使われる。
  name = "${var.project_name}-app-service"

  # タスク／サービスを実行する ECS クラスター（ecs.tf で作成したもの）。
  cluster = aws_ecs_cluster.main.id

  # 起動するコンテナの定義。タスク定義のリビジョン ARN を指定すると、その構成でタスクがスケジュールされる。
  task_definition = aws_ecs_task_definition.app.arn

  # Fargate でのマネージド起動。EC2 インスタンスのプロビジョニングは不要。
  launch_type = "FARGATE"

  # 常時維持するタスク数。学習・検証用途のため最小の 1 に固定している。
  desired_count = 1

  # awsvpc モード時の Subnet・Security Group・パブリック IP の指定。
  network_configuration {
    # Public Subnet に配置し、NAT なしでインターネット（ECR pull 等）に到達できるようにする。
    subnets = [
      aws_subnet.public_1a.id,
      aws_subnet.public_1c.id
    ]

    # アプリの待ち受けポートや RDS 向け等、security_group.tf で定義した ECS 用ルールを適用する。
    security_groups = [
      aws_security_group.ecs.id
    ]

    # ENI にパブリック IPv4 を割り当て、上記 Public Subnet + IGW 経由で外向き通信を可能にする。
    assign_public_ip = true
  }

  tags = {
    # AWS コンソール上で識別しやすい Name タグ。
    Name = "${var.project_name}-app-service"
  }
}
