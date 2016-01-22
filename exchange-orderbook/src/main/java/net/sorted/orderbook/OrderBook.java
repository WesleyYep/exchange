package net.sorted.orderbook;

import java.util.List;

public interface OrderBook {

    MatchedTrades addOrder(Order order);

    void removeOrder(String orderId);

    MatchedTrades modifyOrder(String orderId, long size);

    double getPriceAtLevel(Side side, int level);

    long getSizeAtLevel(Side side, int level);

    List<Order> getAllOrdersForSide(Side side);

    Order getOrder(String orderId);

    OrderBookSnapshot getSnapshot();
}
