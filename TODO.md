

# Core
         
* Add order status to the Order object and update the status as part of its life cycle (sending order update messages)

* Order status update - order status to update and be published to clients.

* Order search - REST endpoint to get all orders. Search on time span with default of today.

# Web

* OrderBookSnapshot REST endpoint. Get the snapshot of an orderbook for the instrument and return as a JSON object.

* Order update publisher

* Order search - REST endpoint to get all orders. Search on time span with default of today.

# Client

* Add dropdown to choose the instrument. The whole screen should change to use the orderbook and 
connections for that instrument. Old websockets should be closed when components not visible

* URLs need to be passed into all the components (getInstrumentREST, publictradesFeed, privateTradesFeed, snapshotFeed, getSnapshot)

* When Snapshot REST endpoint is available, use it to initialise the Snapshot component

# Ops

* Encrypted connection to Rabbit

* Rabbit high availability.

# Build

* Local dev build should not require the swarm (no local maven, npm, docker registry). It should not push any artifacts, just use the locally built 
or downloaded ones.

| File                                     | Why                                                 | Resolution                                                 |
|==========================================|=====================================================|============================================================|                
|  docker/jenkins-slave/Dockerfile         | depends on image in local registry                  | This is not needed by local dev so can remain              |                   
|  docker/jenkins-slave/settings.xml       | points to local maven repo                          | This is not needed by local dev so can remain. Need to     |
|                                          |                                                     | add notes for local dev to use local repo.                 |
|  docker/nocluster/docker-compose.xml     | points to images in local registry                  | Leave for now. Could change to local copies and pull first |
|                                          |                                                     | but no need for now                                        |
|  exchange-core/pom.xml                   |flyway.url points to local swarm address for DB      | This can be overridden using -D. This can change from      |
|                                          |                                                     | machine to machine so note should be made to override this |
|                                          |                                                     | somewhere else                                             |
   
   
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
    