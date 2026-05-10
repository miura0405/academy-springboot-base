# Amazon ECS クラスター。
# タスク／サービスをひとまとめに論理グループとして管理するコンテナ実行基盤。本ファイルではクラスターのみ作成し、
# Task Definition・Service は別リソースまたは手作業でこのクラスター名を指定してデプロイする想定。
resource "aws_ecs_cluster" "main" {
  name = "${var.project_name}-cluster"

  tags = {
    Name = "${var.project_name}-cluster"
  }
}