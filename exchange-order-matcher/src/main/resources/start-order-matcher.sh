#!/bin/bash -eux

# This is to wait for RabbitMQ to be available. Need to put in a better test (eg netcat)
sleep 5

PARAMS=""
if [[ ! -z $RABBIT_HOSTNAME ]]; then
  PARAMS="$PARAMS -Drabbit.hostname=$RABBIT_HOSTNAME"
fi

if [[ ! -z $INSTRUMENTS ]]; then
  PARAMS="$PARAMS -DinstrumentCSL=$INSTRUMENTS"
fi

echo "PARAMS=$PARAMS"
java $PARAMS -jar exchange-order-matcher.jar