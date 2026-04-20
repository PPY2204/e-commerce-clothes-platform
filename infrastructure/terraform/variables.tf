variable "aws_region" {
  description = "AWS region to deploy resources in"
  type        = "string"
  default     = "us-east-1"
}

variable "instance_name" {
  description = "Value of the Name tag for the EC2 instance"
  type        = "string"
  default     = "YamateeInstance"
}
