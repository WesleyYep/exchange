package net.sorted.orderbook;

import java.util.LinkedList;
import java.util.List;

public class OrdersAtPrice {
    private List<Order> orders = new LinkedList<Order>();

    private long quantityAtPrice;

    public void addOrder(Order order) {
        orders.add(order);
        quantityAtPrice = quantityAtPrice + order.getQuantity();
    }

    public void removeOrder(Order order) {
        orders.remove(order);
        quantityAtPrice = quantityAtPrice - order.getQuantity();
    }


    public List<Order> getOrders() {
        return orders;
    }

    public long getQuantity() {
        return quantityAtPrice;
    }
}
