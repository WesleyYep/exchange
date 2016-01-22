package net.sorted.exchange;


import net.sorted.exchange.orderbook.OrderBookSnapshot;

public interface OrderSnapshotPublisher {
    void publishSnapshot(OrderBookSnapshot snapshot);
}
