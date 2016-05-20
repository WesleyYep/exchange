package net.sorted.exchange.orders.orderbook;

import java.util.List;
import net.sorted.exchange.orders.domain.Order;
import net.sorted.exchange.orders.domain.Side;

public interface OrderBook {

    MatchedTrades addOrder(Order order);

    void removeOrder(Long orderId);

//    MatchedTrades modifyOrder(Long orderId, long size);

    double getPriceAtLevel(Side side, int level);

    long getSizeAtLevel(Side side, int level);

    List<Order> getAllOrdersForSide(Side side);

    Order getOrder(Long orderId);

    OrderBookSnapshot getSnapshot();

    String getInstrumentId();
}
