package net.sorted.exchange.orderbook;

import java.util.*;
import net.sorted.exchange.domain.Order;

public class OrdersForSide {
    private final TreeMap<Double, OrdersAtPrice> priceToOrderAtPrice;
    private final Map<String, Order> idToOrder = new HashMap<>();

    private final Double worstPrice;

    public OrdersForSide(boolean reverse) {
        if (reverse) {
            priceToOrderAtPrice = new TreeMap<Double, OrdersAtPrice>(Collections.reverseOrder());
            worstPrice = new Double(0);
        } else {
            priceToOrderAtPrice = new TreeMap<Double, OrdersAtPrice>();
            worstPrice = Double.MAX_VALUE;
        }
    }

    public void addOrder(Order order) {

        Double price = new Double(order.getPrice());
        OrdersAtPrice orders = priceToOrderAtPrice.get(price);
        if (orders == null) {
            orders = new OrdersAtPrice();
            priceToOrderAtPrice.put(price, orders);
        }

        orders.addOrder(order);
        idToOrder.put(order.getId(), order);
    }

    public void removeOrder(String orderId) {
        Order order = idToOrder.remove(orderId);
        if (order != null) {
            Double price = new Double(order.getPrice());
            OrdersAtPrice orders = priceToOrderAtPrice.get(price);
            if (orders != null) {
                orders.removeOrder(order);
                if (orders.getQuantity() == 0) {
                    priceToOrderAtPrice.remove(price);
                }
            }
        }
    }

    /* NB, removed and added so that it goes at the end of the list of orders so modified trades trade after unmodified */
    public void modifyOrder(String orderId, long size) {
        Order order = idToOrder.get(orderId);
        if (order != null) {
            Double price = new Double(order.getPrice());
            OrdersAtPrice orders = priceToOrderAtPrice.get(price);
            orders.removeOrder(order);
            Order n = new Order(order.getId(), order.getPrice(), order.getSide(), size, order.getSymbol(), order.getClientId());
            orders.addOrder(n);
            idToOrder.put(orderId, n);
        }
    }

    public List<Order> getOrdersAtLevel(int level) {
        List<Order> orders = new ArrayList<>();

        int l = level - 1;
        List<Double> orderedPrices = new ArrayList<Double>(priceToOrderAtPrice.keySet());
        if (l < orderedPrices.size()) {
            Double price = orderedPrices.get(l);
            orders = priceToOrderAtPrice.get(price).getOrders();
        }
        return orders;
    }

    public List<Order> getOrdersByLevelAndTime() {
        List<Order> orders = new ArrayList<>();

        for (OrdersAtPrice atPrice : priceToOrderAtPrice.values()) {
            orders.addAll(atPrice.getOrders());
        }

        return orders;
    }

    public List<Order> getOrdersAtOrUnderPrice(Double price) {
        List<Order> matching = new ArrayList<>();
        List<Double> orderedPrices = new ArrayList<Double>(priceToOrderAtPrice.keySet());
        for (Double p : orderedPrices) {
            if (p.compareTo(price) <= 0) {
                matching.addAll(priceToOrderAtPrice.get(p).getOrders());
            }
        }

        return matching;
    }

    public List<Order> getOrdersAtOrOverPrice(Double price) {
        List<Order> matching = new ArrayList<>();
        List<Double> orderedPrices = new ArrayList<Double>(priceToOrderAtPrice.keySet());
        for (Double p : orderedPrices) {
            if (p.compareTo(price) >= 0) {
                matching.addAll(priceToOrderAtPrice.get(p).getOrders());
            }
        }

        return matching;
    }


    public Double getPriceAtLevel(int level) {
        int l = level - 1;
        List<Double> orderedPrices = new ArrayList<Double>(priceToOrderAtPrice.keySet());
        if (l >= orderedPrices.size()) {
            return worstPrice;
        } else {
            Double price = orderedPrices.get(l);
            return price;
        }
    }

    public long getSizeAtLevel(int level) {
        int l = level - 1;
        List<OrdersAtPrice> ordersAtPrice = new ArrayList<OrdersAtPrice>(priceToOrderAtPrice.values());
        if (l >= ordersAtPrice.size()) {
            return 0;
        } else {
            return ordersAtPrice.get(l).getQuantity();
        }
    }

    public Order getOrder(String orderId) {
        return idToOrder.get(orderId);
    }
}
