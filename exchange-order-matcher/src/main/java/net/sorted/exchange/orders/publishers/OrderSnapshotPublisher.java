package net.sorted.exchange.orders.publishers;


import net.sorted.exchange.orders.orderbook.OrderBookSnapshot;

public interface OrderSnapshotPublisher {
    void publishSnapshot(OrderBookSnapshot snapshot);
}
