package net.sorted.exchange.orderprocessor;


import net.sorted.exchange.domain.Order;
import net.sorted.exchange.orderbook.OrderBookSnapshot;

public interface OrderProcessor {
    void submitOrder(Order order);
    void updateOrder(Order order);
    void cancelOrder(Order order);

    OrderBookSnapshot getSnapshot();
}
