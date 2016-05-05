package net.sorted.exchange.orders.dao;


import java.util.HashMap;
import java.util.Map;
import net.sorted.exchange.orders.domain.Order;

public class OrderDaoInMemory implements OrderDao {

    private int currentId = 0;
    private Map<Long, Order> idToOrder = new HashMap<>();

    @Override
    public long getNextOrderId() {
        return currentId++;
    }

    @Override
    public void addOrder(Order o) {
        idToOrder.put(o.getId(), o);
    }

    @Override
    public Order getOrder(long orderId) {
        return idToOrder.get(orderId);
    }
}
