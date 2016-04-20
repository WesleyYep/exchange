#!/bin/bash -eux

SRC=${PWD}
CMD="mvn install"

echo "Num args = $#"

if [ $# -ge 1 ]; then
	CMD=$*
fi

docker run --name build -it -v ${SRC}:/src -v ${HOME}/.m2/respository:/maven-repository -v /var/run/docker.sock:/var/run/docker.sock --rm 192.168.160.235:5000/exchangebuild $CMD
