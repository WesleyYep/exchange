#!/bin/bash -eux
docker run -it -v ${PWD}:/usr/src/mymaven -w /usr/src/mymaven maven:3.3.3-jdk-8 mvn package
