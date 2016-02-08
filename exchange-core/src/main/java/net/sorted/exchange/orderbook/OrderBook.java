package net.sorted.exchange.orderbook;

import java.util.List;
import net.sorted.exchange.domain.Order;
import net.sorted.exchange.domain.Side;

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
