package net.sorted.exchange.orderprocessor;


import java.util.concurrent.Executor;
import net.sorted.exchange.domain.Order;
import net.sorted.exchange.orderbook.MatchedTrades;
import net.sorted.exchange.orderbook.OrderBook;
import net.sorted.exchange.orderbook.OrderBookSnapshot;
import net.sorted.exchange.publishers.OrderSnapshotPublisher;
import net.sorted.exchange.publishers.PrivateTradePublisher;
import net.sorted.exchange.publishers.PublicTradePublisher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OrderProcessorInMemory implements OrderProcessor {

    private Logger log = LogManager.getLogger(OrderProcessorInMemory.class);

    private final OrderBook orderBook;
    private final PrivateTradePublisher privateTradePublisher;
    private final PublicTradePublisher publicTradePublisher;
    private final OrderSnapshotPublisher snapshotPublisher;

    private final Executor publishExecutor;

    private final Object lock = new Object();

    public OrderProcessorInMemory(OrderBook orderBook,
                                  PrivateTradePublisher privateTradePublisher,
                                  PublicTradePublisher publicTradePublisher,
                                  OrderSnapshotPublisher snapshotPublisher,
                                  Executor publishExecutor) {
        this.orderBook = orderBook;
        this.privateTradePublisher = privateTradePublisher;
        this.publicTradePublisher = publicTradePublisher;
        this.snapshotPublisher = snapshotPublisher;

        this.publishExecutor = publishExecutor;
    }

    @Override
    public void submitOrder(Order order) {

        MatchedTrades matches;
        OrderBookSnapshot snapshot;
        synchronized (lock) {
            matches = orderBook.addOrder(order);
            snapshot = orderBook.getSnapshot();
        }

        publishResultInBackground(matches, snapshot);
    }

    @Override
    public void updateOrder(Order order) {
        MatchedTrades matches;
        OrderBookSnapshot snapshot;
        synchronized (lock) {
            matches = orderBook.modifyOrder(order.getId(), order.getQuantity());
            snapshot = orderBook.getSnapshot();
        }

        publishResultInBackground(matches, snapshot);
    }

    @Override
    public void cancelOrder(Order order) {
        OrderBookSnapshot snapshot;
        synchronized (lock) {
            orderBook.removeOrder(order.getId());
            snapshot = orderBook.getSnapshot();
        }

        snapshotPublisher.publishSnapshot(snapshot);
    }

    @Override
    public OrderBookSnapshot getSnapshot() {
        synchronized (lock) {
            return orderBook.getSnapshot();
        }
    }

    // Publish the results on a different thread
    private void publishResultInBackground(final MatchedTrades matches, final OrderBookSnapshot snapshot) {
        publishExecutor.execute(() -> publishResultNow(matches, snapshot));
    }

    private void publishResultNow(MatchedTrades matches, OrderBookSnapshot snapshot) {
        privateTradePublisher.publishTrades(matches.getAggressorTrades());
        privateTradePublisher.publishTrades(matches.getPassiveTrades());
        publicTradePublisher.publishTrades(matches.getPublicTrades());
        snapshotPublisher.publishSnapshot(snapshot);

        log.debug("Published matched trades {}", matches);
    }
}
