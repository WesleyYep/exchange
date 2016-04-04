# Building using Docker

The whole build uses docker. A developer can develop exchange with only docker (and an editor) installed on their local machine.

For building locally, there is a build image ( docker/build/ ). This image contains all the tools needed to build 
the source to the final artefacts, which are docker images.


## Using the images

### Locally
1. Create the image used for building

    cd docker/build
    ./build-image.sh
    
2. Build the code using the image

    cd to the root of the project and run docker/build/build.sh
    
    
### In jenkins
   
TODO - write up this better including some screen shots

Install the Docker plugin and following the instructions for adding a slave. 
For the exchangeslave image the Docker Template will need these details
 
image:  192.168.160.235:5000/exchangeslave    -- NB, this is assuming it is in the local registry 192.168.160.235:5000)
Container Settings:
    Docker Command: /start-services.sh
    Remote Filing System Root: /home/jenkins
    Credentials: jenkins/jenkins
    

## Modifying the tools needed to build the project
   
NB - please readup on [Dockerfiles](https://docs.docker.com/engine/reference/builder/) before continuing.
   
The image is based on a core ubuntu image. This is a bare bones wily installation. All tools 
required to build the project should be installed in the Dockerfile. Look at the current Dockerfile for guidance.
    
The image is setup to build the code in the /src folder. This is mapped to the source checked out from git on the 
host in the build.sh script ( -v ${SRC}:/src ). To speed up the maven build, the maven repository is also mapped to 
the local file system (-v ${HOME}/.m2/respository:/maven-repository). This means the files do not have to be downloaded 
each time the image is used (just the first time).     