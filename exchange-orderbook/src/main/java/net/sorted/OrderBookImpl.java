package net.sorted;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.sorted.Side.*;

public class OrderBookImpl implements OrderBook {


    private OrdersForSide bidOrders = new OrdersForSide(true);
    private OrdersForSide askOrders = new OrdersForSide(false);

    private Map<Long, OrdersForSide> orderIdToOrdersForSide = new HashMap<>();

    @Override
    public void addOrder(Order order) {

        Side side = order.getSide();
        OrdersForSide orders = getOrdersForSide(side);
        orders.addOrder(order);
        orderIdToOrdersForSide.put(order.getId(), orders);
    }

    @Override
    public void removeOrder(long orderId) {
        OrdersForSide orders = orderIdToOrdersForSide.get(orderId);
        orders.removeOrder(orderId);
    }

    @Override
    public void modifyOrder(long orderId, long size) {
        OrdersForSide orders = orderIdToOrdersForSide.get(orderId);
        orders.modifyOrder(orderId, size);
    }

    @Override
    public double getPriceAtLevel(Side side, int level) {
        OrdersForSide orders = getOrdersForSide(side);
        return orders.getPriceAtLevel(level);
    }

    @Override
    public long getSizeAtLevel(Side side, int level) {
        OrdersForSide orders = getOrdersForSide(side);
        return orders.getSizeAtLevel(level);
    }

    @Override
    public List<Order> getAllOrdersForSide(Side side) {

        OrdersForSide orders = getOrdersForSide(side);
        return orders.getOrdersByLevelAndTime();
    }

    @Override
    public List<Order> getMatchingOrdersByPrice() {
        List<Order> matching = new ArrayList<>();

        Double bidPrice = bidOrders.getPriceAtLevel(1);
        Double askPrice = askOrders.getPriceAtLevel(1);
        if (bidPrice >= askPrice) {
            // A bid has crossed the market - return all matching ask's in order of time
            matching = askOrders.getOrdersAtOrUnderPrice(bidPrice);
        }

        return matching;
    }

    private OrdersForSide getOrdersForSide(Side side) {
        if (side == BID) {
            return bidOrders;
        } else {
            return askOrders;
        }
    }
}
