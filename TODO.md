

# Core
   
* Add order status to the Order object and update the status as part of its life cycle

* Order status update - order status to update and be published to clients.

* Order search - REST endpoint to get all orders. Search on time span with default of today.

# Web

* OrderBookSnapshot REST endpoint. Get the snapshot of an orderbook for the instrument and return as a JSON object.

* Order search - REST endpoint to get all orders. Search on time span with default of today.

# Client

* Add dropdown to choose the instrument. The whole screen should change to use the orderbook and 
connections for that instrument. Old websockets should be closed when components not visible




# Build

* Local dev build should not require the swarm (no local maven, npm, docker registry). It should not push any artifacts, just use the locally built 
or downloaded ones.

   bin/build.sh                             - uses exchange build from local registry
   docker/build/build-image.sh              - pushes and tags to local registry
   docker/build/Dockerfile                  - relies on base image in local registry
   docker/db/build-image.sh                 - tag and push to local registry
   docker/jenkins-slave/build-image.sh      - tag and push to local registry
   docker/jenkins-slave/Dockerfile          - depends on image in local registry
   docker/jenkins-slave/settings.xml        - points to local maven repo
   docker/nocluster/docker-compose.xml      - points to images in local registry
   exchange-core/pom.xml                    - flyway.url points to local swarm address for DB
   
   
Registry address
192.168.160.235:5000/image-name
or
image-name

Maven Repo
http://192.168.160.235:7070/repository/internal/
or
http://repo1.maven.org/maven2/
   
   

* split config into a new module (exchange-config) that will hold common config (eg the flyway details for the DB container so that db-migrations can share the same details as exchange-core)

* Jenkins jobs into source control

* Dev build for client that does not require rebulding react-clienta and exchange wen and restarting exchange web.
  This will require the client to be able to connect to a running server without the server login
  
* settings.xml files in build and jenkins-slave are hard coded with the devswarm address. 
    need to add maven filtering and do the build / push from maven
    
* exchange-core/src/main/docker/Dockerfile has repository hardcoded    
    
* exchange-web/src/main/docker/Dockerfile has repository hardcoded    

* Archiva config - just need to add Global repository Manager role to guest user Via REST
Start looking around these URLs: 
    http://192.168.160.235:7070/restServices/redbackServices/userService/getUserOperations/guest
    http://192.168.160.235:7070/restServices/redbackServices/userService/getGuestUser
    http://192.168.160.235:7070/restServices
    
    Docs: http://192.168.160.235:7070/#rest-docs-redback-rest-api/index.html
    
    
* Add code coverage and static analysis to build (SonarCube ?)    
    