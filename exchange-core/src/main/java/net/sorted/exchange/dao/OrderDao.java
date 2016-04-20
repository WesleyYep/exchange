package net.sorted.exchange.dao;


import net.sorted.exchange.domain.Order;

public interface OrderDao {
    String getNextOrderId();

    void addOrder(Order o);
    Order getOrder(String orderId);
}
