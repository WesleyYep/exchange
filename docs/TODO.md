

# Presentation

* Layout page nicely
* Add multiple instruments.
    Get list of authorised instruments from server
    Add control to top of screen to switch instrument (eg combo box)

# Docker 

* Create a container for the application
* Create a compose file for the system
* Create a containerised build (this may need to be considered as part of a larger build system with a swarm) 


# Authentication

A single authentication service should be available for authenticating web, REST or any other service.
OpenAM
OpenID
???

The authentication needs to
* Integrate with Spring (Web and Rest and Messaging)
* Be setup as a docker image (and be setup in docker-compose)
* Be fast enough for microservices


# Clustered system

## Clustered back end

* ExchangeNode shard on instruments.
 
## Clustered web
 
Web nodes - cluster for reliability. 
Will need the authentication sorted first. 
Need to setup load balancer




  
