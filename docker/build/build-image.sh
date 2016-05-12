#!/bin/bash -eux

# To push to an external reposotiry,
# call with a parameter which is the tag of where to push this image
# eg
#   build-image.sh 192.168.160.235:5000/exchangebuild
#
# will push the built image to 192.168.160.235:5000/exchangebuild

docker build -t exchangebuild .

if [ $# == 1 ]; then
    TAG=$1
    docker tag exchangebuild $TAG
    docker push $TAG
fi