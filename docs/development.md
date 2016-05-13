
Development of Exchange can be done on a single machine with no external dependencies except for access to maven central,  npm repository and docker hub. 
If all the artifacts required are in internal repositories, then this external access it not required, the build will just need to be configured for the 
internal repositories.


# Pre-requisite software

* docker version 1.10 or above
* editor / IDE

# Building the project

The build is done by creating a docker container using a docker image that is built in the project. No tools other than docker are needed for development. This makes startup time nice and easy but takes a little bit of getting used to.

1. Create the image (that contains the dev tools and will be used to build the code):

    cd docker/build
    ./build-image.sh
    
If you have access to the internal registry, save some time by using the pre-built image:
    
    docker pull 192.168.160.235:5000/exchangebuild
    docker tag 192.168.160.235:5000/exchangebuild exchangebuild
    
2. To build the project, use the build script in the project aprent dir (or sub module)
    
    bin/build.sh
    
Is the equivalent of running 'mvn install' in the current dir.
    
    bin/build.sh -DskipTests clean install
    
Is the equivalent of running 'maven -DskipTests clean install' in the current dir.
    
    

# Running the exchange

After building the project, there will be some docker images available:
 exchange-node
 exchange-web
 exchange-db
 
These, plus a default rabbitMQ image, make up an exchange system. An exchange system is run using docker-compose

    cd docker/localdev
    docker-compose up 

This will start all components and pipe stdout/stderr from each one to the console. Ctrl-C will stop them all.

Run in the background using

    docker-compose up 
    
Look at what is running with

    docker-compose ps
    
Look at logs with
    
    docker-compose logs component-name 


# Running components

During development, it is normal to be working on one component but want the rest of them running. This is done in exchange by starting the components not 
under development in docker and running the one in development in your IDE, from command line etc.

To just start the db

    docker-compose up exchangeDB
    
To just start rabbitMq

    docker-compose up rabbit
    

exchange web and or exchange node can be worked on and will connect to the other components that are already running

## exchange-node

The program arguments are

| -Drabbit.hostname             | ip or hostname of the rabbit MQ server. This will be localhost on linux or the ip of your VM on Mac or Windows        |
| -Dspring.datasource.url       | The JDBC URL of the database.                                                                                         |
| -DinstrumentCSL               | Comma separated list of instruments (eg AMZN,GOOG)                                                                    |

## exchange-web
   
The program arguments are

| -Drabbit.hostname             | ip or hostname of the rabbit MQ server. This will be localhost on linux or the ip of your VM on Mac or Windows        |
| -DinstrumentCSL               | Comma separated list of instruments (eg AMZN,GOOG)                                                                    |
   

## DB
The database is a postgres DB running on linux. The database is called exchange and has a user with all privileges; exchange_app/exchange_app

### starting

To just start the db

    docker-compose up exchangeDB
    
### initialising

The database structure is seupt using [flyway](https://flyway.org/). The migration scripts are in exchange-dbmigrations/src/main/resources/db/migration

### connecting using tools
   
The DB accepts connections on port 5432. 
The JDBC url is:
   
    jdbc:postgresql://192.168.99.100:5432/exchange
    
    
## RabbitMq
    
    
### starting

    docker-compose up rabbit

### management console

The RabbitMQ manager is available on
    
    IP:9393
    
Where IP is the ip of the docker machine (localhost on linux, $DOCKER_HOST on mac and windows)     
