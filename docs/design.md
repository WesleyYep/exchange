
# sorted/Exchange

An example exchange to be used as part of a test trading system


## Modules

### exchange-core
This has the core code for the exchange. The main components are 
OrderBook
OrderProcessor
ExchangeNode

[Full docs](docs/exchange-core.md)

### exchange-web
This has the web layer or the system. This comprises of
* REST API
* Websockets
* Web Security

[Full Docs](docs/exchange-web.md)

## Operations

The systems is intended to be deployed in docker containers.

### Images

sortednet/exchange-node

This runs an exchange node. This connects to RabbitMQ at rabbit_1.sorted.net (default value)

Config
System properties have their defaults set in exchange.properties but can be overriden by environment variables

| Variable            | Default             | Description                                                 |
|---------------------|---------------------|-------------------------------------------------------------|
| RABBIT_HOSTNAME     | rabbit_1.sorted.net | hostname of rabbit server                                   |
| INSTRUMENTS         | AMZN,GOOG           | Comma separated list of instruments to support on this node |






