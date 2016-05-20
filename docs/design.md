
# sorted/Exchange

An example trading exchange to be used as part of a test trading system


## Modules

### exchange-order-matcher
This has the  code for the orderbooks and order matching. The main components are 
OrderBook
OrderProcessor
OrderMatcher

[Full docs](exchange-order-matcher.md)

### exchange-web
This has the web layer or the system. This comprises of
* REST API
* Websockets
* Web Security

[Full Docs](exchange-web.md)

## Operations

The system is intended to be deployed in docker containers.

### Docker Images

sortednet/exchange-node

This runs an exchange node. This connects to RabbitMQ at rabbit_1.sorted.net (default value)

Config
System properties have their defaults set in exchange.properties but can be overriden by environment variables

| Variable            | Default             | Description                                                 |
|---------------------|---------------------|-------------------------------------------------------------|
| RABBIT_HOSTNAME     | rabbit_1.sorted.net | hostname of rabbit server                                   |
| INSTRUMENTS         | AMZN,GOOG           | Comma separated list of instruments to support on this node |






