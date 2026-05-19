# VPC・Subnet・Internet Gateway・Route Table など、ネットワーク基盤を定義するファイル。
# この構成では 1 つの VPC の中に、外部公開向けの Public Subnet と、
# DB などを置く想定の Private Subnet を複数 AZ に分けて作成する。

# アプリケーション全体を配置する VPC。
# VPC は AWS 上の論理的なネットワーク境界で、Subnet や Security Group はこの中に作られる。
resource "aws_vpc" "main" {
  # VPC 全体で利用するプライベート IPv4 アドレス範囲。
  # ここで確保した範囲を、後続の public/private subnet に分割して割り当てる。
  cidr_block = var.vpc_cidr

  # VPC 内で AWS 提供 DNS による名前解決を有効化する。
  # RDS や ECS など、DNS 名で接続するサービスを使う場合に必要になる。
  enable_dns_support = true

  # VPC 内で起動したリソースに DNS ホスト名を割り当てられるようにする。
  # Public IP を持つ EC2 などで public DNS 名を利用したい場合にも関係する。
  enable_dns_hostnames = true

  tags = {
    # AWS コンソール上で識別しやすい Name タグ。
    # project_name を接頭辞にして、他プロジェクトのリソースと混ざりにくくしている。
    Name = "${var.project_name}-vpc"
  }
}

# 1 つ目の Public Subnet。
# Internet Gateway へのルートを持つため、ECS Service や ALB など外部公開するリソースの配置先に使える。
resource "aws_subnet" "public_1a" {
  # この Subnet を所属させる VPC。
  vpc_id = aws_vpc.main.id

  # Public Subnet 用の IPv4 アドレス範囲。
  # VPC の CIDR に含まれ、他の Subnet と重複しない値にする必要がある。
  cidr_block = var.public_subnet_cidr

  # 配置先の Availability Zone。既定では ap-northeast-1a。
  availability_zone = var.availability_zone

  # true にすると、この Subnet で起動した ENI/EC2 に Public IP が自動付与される。
  # Internet Gateway へのルートと組み合わせて、インターネットから到達可能な Subnet になる。
  map_public_ip_on_launch = true

  tags = {
    Name = "${var.project_name}-public-subnet-1a"
  }
}

# Internet Gateway。
# VPC とインターネットを接続するための出入口で、Public Subnet の 0.0.0.0/0 ルートの向き先になる。
resource "aws_internet_gateway" "main" {
  # Internet Gateway を取り付ける VPC。
  # VPC に attach されていない Internet Gateway は通信経路として使えない。
  vpc_id = aws_vpc.main.id

  tags = {
    Name = "${var.project_name}-igw"
  }
}

# Public Subnet 用の Route Table。
# Subnet と関連付けることで、その Subnet 内の通信がどの経路を使うかを決める。
resource "aws_route_table" "public" {
  # Route Table を作成する VPC。
  # Route Table は VPC 単位のリソースで、別 VPC の Subnet には関連付けできない。
  vpc_id = aws_vpc.main.id

  tags = {
    Name = "${var.project_name}-public-rt"
  }
}

# Public Subnet からインターネットへ出るためのデフォルトルート。
# 0.0.0.0/0 は「VPC 内のローカルルートに一致しない全 IPv4 宛先」を意味する。
resource "aws_route" "public_internet" {
  # ルートを追加する Route Table。
  route_table_id = aws_route_table.public.id

  # 全 IPv4 宛の通信を対象にする。
  destination_cidr_block = "0.0.0.0/0"

  # 宛先が VPC 外の場合、Internet Gateway へ転送する。
  gateway_id = aws_internet_gateway.main.id
}

# 1 つ目の Public Subnet と Public Route Table の関連付け。
# この関連付けにより public_1a が Internet Gateway 経由の通信経路を使う。
resource "aws_route_table_association" "public_1a" {
  subnet_id      = aws_subnet.public_1a.id
  route_table_id = aws_route_table.public.id
}

# 2 つ目の Public Subnet。
# 別 AZ に配置することで、将来的に ALB/ECS などを複数 AZ 構成にしやすくしている。
resource "aws_subnet" "public_1c" {
  # main VPC 内に作成する。
  vpc_id = aws_vpc.main.id

  # 1c 側 Public Subnet の CIDR。
  cidr_block = var.public_subnet_1c_cidr

  # 既定では ap-northeast-1c。
  availability_zone = var.availability_zone_1c

  # Public Subnet として扱うため、起動時の Public IP 自動付与を有効にする。
  map_public_ip_on_launch = true

  tags = {
    Name = "${var.project_name}-public-subnet-1c"
  }
}

# 1 つ目の Private Subnet。
# RDS など、インターネットから直接到達させたくないリソースの配置先を想定している。
resource "aws_subnet" "private_1a" {
  # main VPC 内に作成する。
  vpc_id = aws_vpc.main.id

  # 1a 側 Private Subnet の CIDR。
  cidr_block = var.private_subnet_1a_cidr

  # Public Subnet と同じ 1a に配置しつつ、ルートと Public IP 設定で用途を分ける。
  availability_zone = var.availability_zone

  # Private Subnet として扱うため、起動時の Public IP 自動付与を無効にする。
  map_public_ip_on_launch = false

  tags = {
    Name = "${var.project_name}-private-subnet-1a"
  }
}

# 2 つ目の Private Subnet。
# RDS の Multi-AZ や、ECS タスクの冗長配置などに使える別 AZ 側の非公開 Subnet。
resource "aws_subnet" "private_1c" {
  # main VPC 内に作成する。
  vpc_id = aws_vpc.main.id

  # 1c 側 Private Subnet の CIDR。
  cidr_block = var.private_subnet_1c_cidr

  # 既定では ap-northeast-1c。
  availability_zone = var.availability_zone_1c

  # Private Subnet として扱うため、Public IP は自動付与しない。
  map_public_ip_on_launch = false

  tags = {
    Name = "${var.project_name}-private-subnet-1c"
  }
}

# 2 つ目の Public Subnet と Public Route Table の関連付け。
# public_1a と同じ Route Table を使わせることで、public_1c も Internet Gateway 経由で通信できる。
resource "aws_route_table_association" "public_1c" {
  subnet_id      = aws_subnet.public_1c.id
  route_table_id = aws_route_table.public.id
}
