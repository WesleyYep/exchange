#!/bin/bash -eux

curl -H "Content-Type: application/json" --data @order.json -u doug:password http://localhost:8080/orders
