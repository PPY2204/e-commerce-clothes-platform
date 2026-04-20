# Yamatee Club Infrastructure as Code (Terraform)
# This module provisions the base AWS infrastructure for the e-commerce platform.

terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

# 1. Network Layer (VPC)
resource "aws_vpc" "main" {
  cidr_block = "10.0.0.0/16"
  enable_dns_hostnames = true
  tags = {
    Name = "yamatee-vpc"
  }
}

# 2. Security Layer (WAF/Security Groups)
resource "aws_security_group" "allow_web" {
  name        = "allow_web_traffic"
  description = "Allow inbound HTTPS traffic"
  vpc_id      = aws_vpc.main.id

  ingress {
    description      = "HTTPS from anywhere"
    from_port        = 443
    to_port          = 443
    protocol         = "tcp"
    cidr_blocks      = ["0.0.0.0/0"]
  }

  egress {
    from_port        = 0
    to_port          = 0
    protocol         = "-1"
    cidr_blocks      = ["0.0.0.0/0"]
  }
}

# 3. Compute Layer (EKS Cluster Placeholder)
# This would normally define the Kubernetes cluster nodes.
resource "aws_instance" "app_server" {
  ami           = "ami-0c55b159cbfafe1f0" # Ubuntu 22.04 LTS
  instance_type = "t3.medium"

  vpc_security_group_ids = [aws_security_group.allow_web.id]

  tags = {
    Name = "yamatee-api-gateway"
  }
}

# 4. Database Layer (DynamoDB)
resource "aws_dynamodb_table" "products" {
  name           = "Products"
  billing_mode   = "PAY_PER_REQUEST"
  hash_key       = "productId"

  attribute {
    name = "productId"
    type = "S"
  }
}
