package net.sorted;

import java.util.List;

public interface OrderBook {

    void addOrder(Order order);

    void removeOrder(long orderId);

    void modifyOrder(long orderId, long size);

    double getPriceAtLevel(Side side, int level);

    long getSizeAtLevel(Side side, int level);

    List<Order> getAllOrdersForSide(Side side);

    List<Order> getMatchingOrdersByPrice();
}
