#!/bin/bash -eux

cd build
./build-image.sh

cd ../db
./build-image.sh
