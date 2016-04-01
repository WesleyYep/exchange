#!/bin/bash -eux

service docker start && /usr/sbin/sshd -D
