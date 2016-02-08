package net.sorted.exchange.publishers;


import net.sorted.exchange.orderbook.OrderBookSnapshot;

public interface OrderSnapshotPublisher {
    void publishSnapshot(OrderBookSnapshot snapshot);
}
