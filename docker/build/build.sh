#!/bin/bash -eux

SRC=${PWD}

if [ $# == 1 ]; then
	SRC=$1
	if [ ! -d $SRC ]; then
		echo "Specified source dir does not exist ( $SRC )"
		exit 1
	fi
fi

docker run --name build -it -v ${SRC}:/src -v ${HOME}/.m2/respository:/maven-repository -v /var/run/docker.sock:/var/run/docker.sock --rm 192.168.160.235:5000/exchangebuild
