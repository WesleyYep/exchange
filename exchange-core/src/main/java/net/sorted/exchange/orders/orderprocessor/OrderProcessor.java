package net.sorted.exchange.orders.orderprocessor;


import net.sorted.exchange.orders.domain.Order;
import net.sorted.exchange.orders.domain.OrderType;
import net.sorted.exchange.orders.domain.Side;
import net.sorted.exchange.orders.orderbook.OrderBookSnapshot;

public interface OrderProcessor {
    long submitOrder(double price, Side side, long quantity, String symbol, String clientId, OrderType type);
    void updateOrder(Order order);
    void cancelOrder(Order order);

    OrderBookSnapshot getSnapshot();
}