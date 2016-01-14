package net.sorted;

import java.util.LinkedList;
import java.util.List;

public class OrdersAtPrice {
    private List<Order> orders = new LinkedList<Order>();

    long size;

    public void addOrder(Order order) {
        orders.add(order);
        size = size + order.getSize();
    }

    public void removeOrder(Order order) {
        orders.remove(order);
        size = size - order.getSize();
    }


    public List<Order> getOrders() {
        return orders;
    }

    public long getSize() {
        return size;
    }
}
