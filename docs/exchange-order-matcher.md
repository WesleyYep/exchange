# General Design

         [ RabbitMQ ]
      ==ExchangeMessage.Order===> SubmitOrderReceiver --Order---> OrderProcessor ---> OrderBook                     [ RabbitMQ ]
           (protobuf)                                                            ---> privateTradePublisher  ===ExchangeMessage.Trade===>              
                                                                                 ---> publicTradePublisher   ===ExchangeMessage.Trade===> 
                                                                                 ---> snapshotPublisher      ===ExchangeMessage.Snapshot===> 
                                                                                                                 ( protobuf )

# Components

## OrderMatcher

This component is multi-threaded and has a RabbitMQ messaging API.
This is a Spring main class. It loads the application context and starts the message receivers.  

There may be multiple OrderMatchers setup to shard the processing by instrument.

## SubmitOrderReceiver
This handles the RabbitMQ side and delegates the business logic out to an OrderProcessor

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


## Orderbook

Represents a single orderbook. The OrderBook holds a collection of unfilled orders for a single common instrument and matches all new orders to the existing ones.

This is a single threaded implementation with a java API.NB, this must be single threaded, ordered must be procesedd in the order that they arrive.

    -------------        -----------------        -----------------
    | OrderBook | --2--> | OrdersForSide | --*--> | OrdersAtPrice |
    -------------        -----------------        -----------------

OrderBook holds 2 OrdersForSide.

OrdersForSide is a map of orders for a side (buy or sell). The map is price to OrdersAtPrice

OrdersForPrice is a list of orders at a price in the time order in which they were placed.

MatchingOrders is a list of buys and sells that match as a price. Only filled or partial fills are returned.






# OrderMatcher API

## Messages

Messages are encoded using protobuf (see https://github.com/google/protobuf)


Order
| Member         | Description                                                          |
|----------------|----------------------------------------------------------------------|
| orderId        | The order. Will be ignored for new orders                            |
| correlationid  | Client supplied id - will be matched in corresponding messages       |
| clientId       | Client making the order                                              |
| instrument     | id of the instrument the order is for                                |
| quantity       | quantity of the order                                                |
| buy/sell       | buy or sell                                                          |
| price          | price to pay                                                         |
| type           | limit or FillOrKill                                                  |
| state          | new, submitted, filled, cancelled                                    | 
    
    
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

        
OrderBookSnapshot - lists are ordered by price and time of subbmission.
    clientId
    correlationId
    List<Order> buys
    List<Order> sells        
        

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
            "tradeDate":"millis since epoch"
        }

Snapshot
    The snapshot state of the orderbook. 
    
    Messages are JSON objects, e.g.
    Each side is an array of price/quantity in level order (for buy level 1 is the highest price, for sell level 1 is the lowest price)
    {
        instrumentId: "AMZN" 
        buy: [  
            { price: 1.1, quantity: 99 },  
            { price: 1.0, quantity: 50 }
        ],
        sell: [  
            { price: 1.2, quantity: 100 },  
            { price: 1.3, quantity: 200 }
        ]
      
    }

OrderUpdate
    Order messages - all parts of the message are filled in and correct.

OrderSnapshot
    OrderBookSnapshot

# Configuration

The instruments that an OrderMatcher keeps an OrderBook for are configured in using the System property 'instrumentCSL' which is setup in exchange.properties
 
The hostname of the rabbit s 

## System properties

| property name             | property file                | notes                                                                  |
|---------------------------|------------------------------|------------------------------------------------------------------------|
| instrumentCSL             | exchange.properties          | comma separated list of ticker symbols of the instruments on this node |
| rabbit.hostname           | exchange.properties          | The hostname of the Rabbit MQ server                                   |