

# Core

* Add order status to the Order object and update the status as part of its life cycle

* Persist Orders once they have been put in the orderbook and matched (partial or full)

* Order status update - order status to update and be published to clients.

* Order search - REST endpoint to get all orders. Search on time span with default of today.

# Web

* OrderBookSnapshot REST endpoint. Get the snapshot of an orderbook for the instrument and return as a JSON object.

* Order search - REST endpoint to get all orders. Search on time span with default of today.

# Client

* Add dropdown to choose the instrument. The whole screen should change to use the orderbook and 
connections for that instrument. Old websockets should be closed when components not visible




# Build

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
    