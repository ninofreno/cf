#!/bin/sh

curl -sH "Content-Type: application/json" -X POST -d '{"userId":"john","movieId":"the blues brothers","rating":5}' 'http://localhost:10080/event'
curl -sH "Content-Type: application/json" -X POST -d '{"userId":"elwood","movieId":"ghostbusters","rating":5}' 'http://localhost:10080/event'
curl -sH "Content-Type: application/json" -X POST -d '{"userId":"elwood","movieId":"the blues brothers","rating":4}' 'http://localhost:10080/event'
curl -sH "Content-Type: application/json" -X POST -d '{"userId":"bill","movieId":"ghostbusters","rating":4}' 'http://localhost:10080/event'
