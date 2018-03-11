#!/bin/sh

curl -s 'http://localhost:10080/user?id=1' | jq -S .

curl -s 'http://localhost:10080/top-k?userId=2&k=10' | jq -S .
