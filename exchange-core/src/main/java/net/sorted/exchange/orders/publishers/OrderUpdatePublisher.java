package net.sorted.exchange.orders.publishers;


import java.util.List;
import net.sorted.exchange.orders.domain.Order;

public interface OrderUpdatePublisher {
    void publishUpdates(List<Order> order);
    void publishUpdate(Order order);
}
