
# sorted/Exchange

An example exchange to be used as part of a test trading system


## Modules

### Orderbook

Represents a single orderbook. This is the book for a single instrument. This is a single threaded implementation with a java API.

    public interface OrderBook {
    
        void addOrder(Order order);
    
        void removeOrder(long orderId);
    
        void modifyOrder(long orderId, long size);
    
        double getPriceAtLevel(char side, int level);
    
        long getSizeAtLevel(char side, int level);
    
        List<Order> getAllOrdersForSide(char side);

        MatchingOrders getMatchingOrdersByPrice();
    }


    -------------        -----------------     -----------------
    | OrderBook | --2--> | OrdersForSide | --> | OrdersAtPrice |
    -------------        -----------------     -----------------

OrderBook holds 2 OrdersForSide.

OrdersForSide is a map of orders for a side (buy or sell). The map is price to OrdersAtPrice

OrdersForPrice is a list of orders at a price in the time order in which they were placed.

MatchingOrders is a list of buys and sells that match as a price. Only filled or partial fills are returned.


### OrderProcessor
The Order processor interacts with a single OrderBook, adding, deleting and modifying Orders for the OrderTypes 'Fill of Kill' and 'Limit Order'. 
Matched orders are submitted to the settlement system

#### Fill or Kill
Add the order
Get matching orders.
If the order is fully matched, settle
If the order is partially matched, settle on partial and modify Order to buy/sell the remainder.
If the order is not matched, delete it

#### Limit Order
A limit order is put on the book until killed.

### Exchange

This is a collection of OrderProcessors. It accepts general order messages and utilises the specific order processor. 
This component is multi-threaded and has a RabbitMQ messaging API.

API
===

Messages
--------
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
    
SnapshotRequest
    clientId
    correlationId
    instrument
    
OrderBookSnapshot - lists are ordered by price and time of subbmission.
    clientId
    correlationId
    List<Order> buys
    List<Order> sells

PriceSubscription
    clientId
    instrument
        
PriceSnapshot
    clientId
    instrument
    bidPrice
    askPrice
        

Queues
------
All three queues are for client -> server communications. 
SubmitOrder - Submit an order for an instrument at a price
    Order messages - Instrument is the Routing Key (to allow consumers to process on specific instruments)
                   - orderId and state is ignored
    
CancelOrder
    Order messages - Only the order id and the correlation id are used. 

ModifyOrder
    Order messages - All information is used except for state
    
SnapshotRequest  - clientId, correlation    

Channels
--------
Channels supply feedback from the server to clients.
Clients should filter messages on the client id

PublicTrade
    Trades are made public on this channel. These do not have trade Ids, orderIds and are rolled up trades (not each partial fill)

    These are JSON objects, e.g.
    
        {
            "instrumentId":"DELL",
            "quantity":1000,
            "price":100.02,
            "tradeDate":"28-01-2016"
        }

OrderUpdate
    Order messages - all parts of the message are filled in and correct.

OrderSnapshot
    OrderBookSnapshot

### Settlement
The settlement system takes in filled orders and creates trades.
The trades can be queried and events are also published for trade creation and state change


