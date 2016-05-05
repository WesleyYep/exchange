package net.sorted.exchange.orders.dao;


import net.sorted.exchange.orders.domain.Order;

public interface OrderDao {
    long getNextOrderId();

    void addOrder(Order o);
    Order getOrder(long orderId);
}
