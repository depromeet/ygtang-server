#!/bin/bash

REGION=ap-northeast-2
AWS_ACCOUNT_ID=106290768882

aws ecr get-login-password --region $REGION | docker login --username AWS --password-stdin $AWS_ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com
