package net.sorted.exchange.orders.publishers;


import java.util.List;
import java.util.Set;
import net.sorted.exchange.orders.domain.Order;

public interface OrderUpdatePublisher {
    void publishUpdates(Set<Order> order);
    void publishUpdate(Order order);
}
