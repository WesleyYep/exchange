
* images built (core and web) need to be pushed

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
    