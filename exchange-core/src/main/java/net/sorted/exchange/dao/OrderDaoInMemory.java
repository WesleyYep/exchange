package net.sorted.exchange.dao;


import java.util.HashMap;
import java.util.Map;
import net.sorted.exchange.domain.Order;

public class OrderDaoInMemory implements OrderDao {

    private int currentId = 0;
    private Map<String, Order> idToOrder = new HashMap<>();

    @Override
    public String getNextOrderId() {
        return ""+currentId++;
    }

    @Override
    public void addOrder(Order o) {
        idToOrder.put(o.getId(), o);
    }

    @Override
    public Order getOrder(String orderId) {
        return idToOrder.get(orderId);
    }
}
