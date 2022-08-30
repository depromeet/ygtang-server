#!/bin/bash

REGION=$1
AWS_ACCOUNT_ID=$2

aws ecr get-login-password --region $REGION | docker login --username AWS --password-stdin $AWS_ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com
