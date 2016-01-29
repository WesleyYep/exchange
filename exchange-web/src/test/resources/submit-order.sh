#!/bin/bash -eux

curl -H "Content-Type: application/json" --data @order.json http://localhost:8080/orders
