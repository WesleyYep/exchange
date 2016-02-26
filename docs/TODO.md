

# Presentation

* Layout page nicely
* Add multiple instruments.
    Get list of authorised instruments from server
    Add control to top of screen to switch instrument (eg combo box)

# Documentation

* Finish docs for current implementation of each of the modules
* Write up target system design (start with architecture, security, clustering etc)
* Write up testing strategy


# Server 

* Add REST service to get the instruments available to a user (initial implementation can just be a fixed set of instruments for every user)
* Order status - Order status updates need to be reported back to order submitter (including the actual assigned order id)
* Implement 'Fill or Kill'

# Docker 

* Create a compose file for clustered system
* Create a containerised build (this may need to be considered as part of a larger build system with a swarm) 


# Authentication

A single authentication service should be available for authenticating web, REST or any other service.
OpenAM
OpenID
???

The authentication needs to
* Integrate with Spring (Web and Rest and Messaging) - need to have web page submit to REST API that is not on the machine it loaded from
* Be setup as a docker image (and be setup in docker-compose)
* Be fast enough for microservices


# Interprocess comms

* Kill json on the server side.
   Use protoc or some other messaging. No json in messages going into ExchangeNodes - only use for web submissions (and get rid there too if possible)


# Clustered system

## Clustered back end

* ExchangeNode - shard on instruments.
* Can look at how to have multiple nodes for a single orderbook (eg using in memory DB).  
 
## Clustered web
 
* Web nodes - cluster for reliability. 
* Will need the authentication sorted first - SSO?. 
* Setup load balancer

## Clustered RabbitMQ
Readup on the docs for doing this.




  
