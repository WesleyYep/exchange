
Development of Exchange can be done on a single machine with no external dependencies except for access to maven central,  npm repository and docker hub. 
If all the artifacts required are in internal repositories, then this external access it not required, the build will just need to be configured for the 
internal repositories.


# Pre-requisite software

* docker version 1.10 or above
* editor / IDE

# Building the project

The build is done by creating a docker container using a docker image that is created in the project

Create the image:

    cd docker/build
    ./build-image.sh
    
If you have access to the internal registry, save some time by using the pre-built image:
    
    docker pull 192.168.160.235:5000/exchangebuild
    docker tag 192.168.160.235:5000/exchangebuild exchangebuild
    
To build the project, use the build script in the project aprent dir (or sub module)
    
    bin/build.sh
    
Is the equivalent of running 'mvn install' in the current dir.
    
    bin/build.sh -DskipTests clean install
    
Is the equivalent of running 'maven -DskipTests clean install' in the current dir.
    
    

# Running the exchange



# Running components

## DB

### starting

### initialising

### connecting using tools
   
    
## RabbitMq
    
### starting

### management console

