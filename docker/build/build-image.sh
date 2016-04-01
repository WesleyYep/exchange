#!/bin/bash -x

docker build -t 192.168.160.235:5000/exchangebuild .  &&  docker push 192.168.160.235:5000/exchangebuild
