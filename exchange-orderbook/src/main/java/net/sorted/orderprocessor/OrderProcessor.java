package net.sorted.orderprocessor;


import net.sorted.orderbook.Order;

public interface OrderProcessor {
    void submitOrder(Order order);
    void updateOrder(Order order);
    void cancelOrder(Order order);

}
