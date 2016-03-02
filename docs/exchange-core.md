# Components

## Orderbook

Represents a single orderbook. This is the book for a single instrument. This is a single threaded implementation with a java API.

    -------------        -----------------        -----------------
    | OrderBook | --2--> | OrdersForSide | --*--> | OrdersAtPrice |
    -------------        -----------------        -----------------

OrderBook holds 2 OrdersForSide.

OrdersForSide is a map of orders for a side (buy or sell). The map is price to OrdersAtPrice

OrdersForPrice is a list of orders at a price in the time order in which they were placed.

MatchingOrders is a list of buys and sells that match as a price. Only filled or partial fills are returned.


## OrderProcessor
The Order processor interacts with a single OrderBook, adding, deleting and modifying Orders for the OrderTypes 'Fill of Kill' and 'Limit Order'. 
The OrderProcessor deals with publishing Trades and snapshots if there is a match.


### Limit Order
A limit order is put on the book until killed.

### Fill or Kill
Add the order
Get matching orders.
If the order is fully matched, settle
If the order is partially matched, settle on partial and modify Order to buy/sell the remainder.
If the order is not matched, delete it


## SubmitOrderReceiver
This handles the RabbitMQ side and delegates the business logic out to an OrderProcessor

## ExchangeNode

This is a collection of SubmitOrderReceivers (by way of a MqOrderReceiver object). It accepts general order messages and utilises the specific order processor. 
This component is multi-threaded and has a RabbitMQ messaging API.

The may be multiple ExchangeNodes setup to shard the processing by instrument.

# ExchangeNode API

## Messages

ExchangeOrder
    orderId 
    correlationid
    clientId
    instrument
    quantity
    buy/sell
    price
    type (limit or FillOrKill)
    state  (open, filled, partial, cancelled)
    
    
OrderBookSnapshot - lists are ordered by price and time of subbmission.
    clientId
    correlationId
    List<Order> buys
    List<Order> sells

PublicTrade
    instrumentId
    quantity
    price
    tradeDate

PrivateTrade
    tradeId
    orderId
    clientId
    instrumentId
    quantity
    price
    tradeDate
        

### Message flow
    TODO - describe ALL message flows
    
ExchangeOrder comes in, 

    if matches then 
        private trades sent to parties involved 
        OrderBookSnapshot + public trades sent to anyone who is listening
    else
        OrderBookSnapshot sent to anyone who is listening
   



## Queues

All three queues are for client -> server communications. 
SubmitOrder - Submit an order for an instrument at a price
    Order messages - Instrument is the Routing Key (to allow consumers to process on specific instruments)
                   - orderId and state is ignored
In RabbitMQ SubmitOrder is a "direct" exchange. Messages are sent to the exchange with a "routing key" of the instrument name. 
The ExchangeNodes only listen for submissions for the instruments they are processing by binding a queue for each instrument using the instrument as the "binding key".
                   
    
CancelOrder
    Order messages - Only the order id and the correlation id are used. 

ModifyOrder
    Order messages - All information is used except for state
    
SnapshotRequest  - clientId, correlation    

## Channels

Channels supply feedback from the server to clients.
Clients should filter messages on the client id

PublicTrade
    Trades are made public on this channel. These do not have trade Ids, orderIds and are rolled up trades (not each partial fill)

    These are JSON objects, e.g.
    
        {
            "instrumentId":"AMZN",
            "quantity":1000,
            "price":100.02,
            "tradeDate":"28-01-2016"
        }

Snapshot
    The snapshot state of the orderbook. 
    
    Messages are JSON objects, e.g.
     Each side is an array of price/quantity in level order (for buy level 1 is the highest price, for sell level 1 is the lowest price)
    {
      "AMZN" {
         buy: [  
          { price: 1.1, quantity: 99 },  
          { price: 1.0, quantity: 50 }
         ],
         sell: [  
          { price: 1.2, quantity: 100 },  
          { price: 1.3, quantity: 200 }
        ]
      }
    }

OrderUpdate
    Order messages - all parts of the message are filled in and correct.

OrderSnapshot
    OrderBookSnapshot

# Configuration

The instruments that an ExchangeNode keeps an OrderBook for are configured in using the System property 'instrumentCSL' which is setup in exchange.properties
 
The hostname of the rabbit s 

## System properties

| property name             | property file                | notes                                                                  |
|---------------------------|------------------------------|------------------------------------------------------------------------|
| instrumentCSL             | exchange.properties          | comma separated list of ticker symbols of the instruments on this node |
| rabbit.hostname           | exchange.properties          | The hostname of the Rabbit MQ server                                   |