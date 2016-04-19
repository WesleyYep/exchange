

# Core

* Get rid of JsonConverter. Switch to protobuf for marshalling in and out of core

* OrderProcessorInMemory.publishResults() should run in a thread (not the one that added and did the matching)

* Order status update - order status to update and be published to clients.

* Order search - REST endpoint to get all orders. Search on time span with default of today.

# Web

* Need to respond to use of protobufs on the PublicTradeListener, PrivateTradeListener and SnapshotListener
  These need to unmarshal from protobuf then encode as json. Not sure if Spring will do the encoding for us or if need to use JAXB/json.
  It would be useful to have a simple websocket client on the command line - look at the python log stuff I did for Alex. This may help.
  
  
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
    