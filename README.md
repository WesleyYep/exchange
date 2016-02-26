# Sorted Exchange

This is a project to experiment with different technologies. The focus of the project is a trading exchange. It is a simple Limit Order exchange that can take orders and match them. It publishes trades from matching orders and snapshots of the order books on chnage.

This is a "Dev Ops" project with each of the elements wrapped in a docker image and the whole system available via docker-compose. Eventually, the build will be 'dockerised' too.

## Details

* [Design docs](docs/design.md) 
* [TODO](docs/TODO.md)


## Quickstart

### Build

    mvn install

### Run 

    cd docker/nocluster
    docker-compose up

If on windows or mac, IP is the ip of the VM ( get the ip of the docker VM by typing 'docker-machine env default' )

On linux, IP is localhost

Webpage available on IP:8888

login as either doug or john with password ‘password’