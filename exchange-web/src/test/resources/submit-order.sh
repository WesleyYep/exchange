#!/bin/bash -eux

curl -H "Content-Type: application/json" --data @order.json -u john:password http://localhost:8080/orders
