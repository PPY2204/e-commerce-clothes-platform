#!/bin/bash
awslocal dynamodb create-table \
    --table-name Products \
    --attribute-definitions \
        AttributeName=id,AttributeType=S \
    --key-schema \
        AttributeName=id,KeyType=HASH \
    --billing-mode PAY_PER_REQUEST \
    --region us-east-1

echo "DynamoDB table 'Products' created successfully in LocalStack."
