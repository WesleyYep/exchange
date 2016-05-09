#!/bin/bash -eux


docker build -t exchangebuild .

if [ $# == 1 ]; then
    docker tag exchangebuild 192.168.160.235:5000/exchangebuild
    docker push 192.168.160.235:5000/exchangebuild
fi