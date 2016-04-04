#!/bin/bash -eux

service docker start

echo "start-services.sh called" >> /tmp/startup.log
service docker status >> /tmp/startup.log

/usr/sbin/sshd -D
