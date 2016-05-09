#!/bin/bash -x

docker build -t exchangedb .
docker tag exchangedb 192.168.160.235:5000/exchangedb
docker push 192.168.160.235:5000/exchangedb
