#!/bin/bash -eux

SRC=${PWD}
CMD="mvn install"

if [ $# == 1 ]; then
	CMD=$1
fi

docker run --name build -it -v ${SRC}:/src -v ${HOME}/.m2/respository:/maven-repository -v /var/run/docker.sock:/var/run/docker.sock --rm 192.168.160.235:5000/exchangebuild $CMD
