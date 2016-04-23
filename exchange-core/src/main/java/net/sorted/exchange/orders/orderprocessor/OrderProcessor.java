package net.sorted.exchange.orders.orderprocessor;


import net.sorted.exchange.orders.domain.Order;
import net.sorted.exchange.orders.orderbook.OrderBookSnapshot;

public interface OrderProcessor {
    void submitOrder(Order order);
    void updateOrder(Order order);
    void cancelOrder(Order order);

    OrderBookSnapshot getSnapshot();
}
