#!/bin/bash -eux

sleep 5

PARAMS=""
if [[ ! -z $RABBIT_HOSTNAME ]]; then
  PARAMS="$PARAMS -Drabbit.hostname=$RABBIT_HOSTNAME"
fi

if [[ ! -z $INSTRUMENTS ]]; then
  PARAMS="$PARAMS -DinstrumentCSL=$INSTRUMENTS"
fi

echo "PARAMS=$PARAMS"
java $PARAMS -jar exchange-web.jar
