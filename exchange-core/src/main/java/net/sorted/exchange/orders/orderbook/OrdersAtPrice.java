package net.sorted.exchange.orders.orderbook;

import java.util.LinkedList;
import java.util.List;
import net.sorted.exchange.orders.domain.Order;

public class OrdersAtPrice {
    private List<Order> orders = new LinkedList<Order>();

    private long quantityAtPrice;

    public void addOrder(Order order) {
        orders.add(order);
        quantityAtPrice += order.getUnfilledQuantity();
    }

    public void removeOrder(Order order) {
        orders.remove(order);
        quantityAtPrice -= order.getUnfilledQuantity();
    }

    public void updateOrder(Order updatedOrder) {
        for (int i=0; i<orders.size(); i++) {
            Order o = orders.get(i);
            if (o.getId() == updatedOrder.getId() ) {
                orders.set(i, updatedOrder);

                long oldUnfilled = o.getUnfilledQuantity();
                long newUnfilled = updatedOrder.getUnfilledQuantity();
                long delta = newUnfilled - oldUnfilled;

                quantityAtPrice += delta;
                break;
            }
        }
    }


    public List<Order> getOrders() {
        return orders;
    }

    public long getQuantity() {
        return quantityAtPrice;
    }


}
