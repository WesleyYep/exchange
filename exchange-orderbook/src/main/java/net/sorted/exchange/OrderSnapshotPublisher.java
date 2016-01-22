package net.sorted.exchange;


import net.sorted.orderbook.OrderBookSnapshot;

public interface OrderSnapshotPublisher {
    void publishSnapshot(OrderBookSnapshot snapshot);
}
