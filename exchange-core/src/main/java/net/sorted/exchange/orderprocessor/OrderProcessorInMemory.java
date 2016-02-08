package net.sorted.exchange.orderprocessor;


import net.sorted.exchange.publishers.OrderSnapshotPublisher;
import net.sorted.exchange.publishers.PrivateTradePublisher;
import net.sorted.exchange.publishers.PublicTradePublisher;
import net.sorted.exchange.orderbook.MatchedTrades;
import net.sorted.exchange.domain.Order;
import net.sorted.exchange.orderbook.OrderBook;
import net.sorted.exchange.orderbook.OrderBookSnapshot;

public class OrderProcessorInMemory implements OrderProcessor {

    private final OrderBook orderBook;
    private final PrivateTradePublisher privateTradePublisher;
    private final PublicTradePublisher publicTradePublisher;
    private final OrderSnapshotPublisher snapshotPublisher;

    private final Object lock = new Object();

    public OrderProcessorInMemory(OrderBook orderBook,
                                  PrivateTradePublisher privateTradePublisher,
                                  PublicTradePublisher publicTradePublisher,
                                  OrderSnapshotPublisher snapshotPublisher) {
        this.orderBook = orderBook;
        this.privateTradePublisher = privateTradePublisher;
        this.publicTradePublisher = publicTradePublisher;
        this.snapshotPublisher = snapshotPublisher;
    }


    @Override
    public void submitOrder(Order order) {

        MatchedTrades matches = null;
        OrderBookSnapshot snapshot = null;
        synchronized (lock) {
            matches = orderBook.addOrder(order);
            snapshot = orderBook.getSnapshot();
        }

        privateTradePublisher.publishTrades(matches.getAggressorTrades());
        privateTradePublisher.publishTrades(matches.getPassiveTrades());
        publicTradePublisher.publishTrades(matches.getPublicTrades());
        snapshotPublisher.publishSnapshot(snapshot);
    }

    @Override
    public void updateOrder(Order order) {
        // TODO - implement
        throw new RuntimeException("Not yet implemented");
    }

    @Override
    public void cancelOrder(Order order) {
        // TODO - implement
        throw new RuntimeException("Not yet implemented");
    }

    @Override
    public OrderBookSnapshot getSnapshot() {
        synchronized (lock) {
            return orderBook.getSnapshot();
        }
    }


}
