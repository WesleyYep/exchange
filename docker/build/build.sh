#!/bin/bash -eux


# docker run --privileged -it --name build -v /Users/dougbarthram/projects/ext/sorted/exchange:/src -v /Users/dougbarthram/.m2/repositoryD:/maven-repository --rm sortednet/build

docker run --privileged --name build -it -v ${PWD}:/src -v ${HOME}/.m2/respositoryD:/root/.m2/repository 10.100.192.200:5000/exchangebuild
