package net.sorted.exchange.orderprocessor;


import net.sorted.exchange.Order;

public interface OrderProcessor {
    void submitOrder(Order order);
    void updateOrder(Order order);
    void cancelOrder(Order order);

}
