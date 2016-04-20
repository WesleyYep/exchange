# Sorted Exchange

This is a project to experiment with different technologies. The focus of the project is a trading exchange. It is a simple Limit Order exchange that can take orders and match them. It publishes trades from matching orders and snapshots of the order books on chnage.

This is a "Dev Ops" project with each of the elements wrapped in a docker image and the whole system available via docker-compose. Eventually, the build will be 'dockerised' too.

## Details

* [Design docs](docs/design.md) 
* [TODO](docs/TODO.md)


## Quickstart

Install docker

### Build

    cd to project root dir
    bin/build.sh

### Run 

    cd docker/localdev
    docker-compose up

Webpage available on IP:8888


If on windows or mac, IP is the ip of the VM ( get the ip of the docker VM by typing 'docker-machine env default' )

On linux, IP is localhost


login as either doug or john with password ‘password’



# Running the application

## ExchangeNode

java -jar exchange-node.jar
  
Two system variables must be set

rabbit.hostname     The hostname of the rabbit MQ server
instrumentCSL       Comma seperated list of instruments to create orderbooks for (eg AMZN,GOOG)

The exchange node will fail to start if these are not set and if a connection cannot be made to the rabbitMQ server


## ExchangeWeb

java -jar exchange-web.jar
  
Two system variables must be set

rabbit.hostname     The hostname of the rabbit MQ server
instrumentCSL       Comma seperated list of instruments to create orderbooks for (eg AMZN,GOOG)

The exchange node will fail to start if these are not set and if a connection cannot be made to the rabbitMQ server

