
# sorted/Exchange

An example exchange to be used as part of a test trading system


## Modules

### Orderbook

Represents a single orderbook. This is the book for a single intrument. This is a single threaded implementation with a java API.

    public interface OrderBook {
    
        void addOrder(Order order);
    
        void removeOrder(long orderId);
    
        void modifyOrder(long orderId, long size);
    
        double getPriceAtLevel(char side, int level);
    
        long getSizeAtLevel(char side, int level);
    
        List<Order> getAllOrdersForSide(char side);

        List<Order> getMatchingOrdersByPrice();
    }


    -------------        -----------------     -----------------
    | OrderBook | --2--> | OrdersForSide | --> | OrdersAtPrice |
    -------------        -----------------     -----------------

OrderBook holds 2 OrdersForSide.

OrdersForSide is a map of orders for a side (bid or ask). The map is price to OrdersAtPrice

OrdersForPrice is a list of orders at a price in the time order in which they were placed.

### Exchange

This is a collection of OrderBooks. It accepts general order messages and utilises the specific orderbook. This component is multi-threaded and has a RabbitMQ messaging API.

The exchange deals with the interaction with the order book, adding orders then processing any matches. It will also deal with the order type 'Fill or kill' and 'Limit'



