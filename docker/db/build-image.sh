#!/bin/bash -x

# To push to an external reposotiry,
# call with a parameter which is the tag of where to push this image
# eg
#   build-image.sh 192.168.160.235:5000/exchangedb
#
# will push the built image to 192.168.160.235:5000/exchangedb

docker build -t exchangedb .

if [ $# == 1 ]; then
    TAG=$1
    docker tag exchangedb $TAG
    docker push $TAG
fi

