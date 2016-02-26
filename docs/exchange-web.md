# Exchange Web Layer

# Design
Springboot webapp
Config files
MQ listeners
REST API

## Websockets

The websocket is available on 

    /exchange

The messages are available on

| channel                     | Notes                                                                      |
|-----------------------------|----------------------------------------------------------------------------|
| /topic/public.trade         | Public trades (visible to everyone)                                        |
| /topic/snapshot             | Snapshots of the order book                                                |
| /topic/public.trade         | Public trades (visible to everyone)                                        |
| /user/topic/private.trade   | this is private to the user and only messages for this user will come here |

Security

## REST API