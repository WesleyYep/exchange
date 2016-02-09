package net.sorted.exchange.orderprocessor;


import net.sorted.exchange.publishers.OrderSnapshotPublisher;
import net.sorted.exchange.publishers.PrivateTradePublisher;
import net.sorted.exchange.publishers.PublicTradePublisher;
import net.sorted.exchange.orderbook.MatchedTrades;
import net.sorted.exchange.domain.Order;
import net.sorted.exchange.orderbook.OrderBook;
import net.sorted.exchange.orderbook.OrderBookSnapshot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OrderProcessorInMemory implements OrderProcessor {

    private Logger log = LogManager.getLogger(OrderProcessorInMemory.class);

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

        publishResult(matches, snapshot);
    }

    @Override
    public void updateOrder(Order order) {
        MatchedTrades matches = null;
        OrderBookSnapshot snapshot = null;
        synchronized (lock) {
            matches = orderBook.modifyOrder(order.getId(), order.getQuantity());
            snapshot = orderBook.getSnapshot();
        }

        publishResult(matches, snapshot);
    }

    @Override
    public void cancelOrder(Order order) {
        MatchedTrades matches = null;
        OrderBookSnapshot snapshot = null;
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


    private void publishResult(MatchedTrades matches, OrderBookSnapshot snapshot) {
        privateTradePublisher.publishTrades(matches.getAggressorTrades());
        privateTradePublisher.publishTrades(matches.getPassiveTrades());
        publicTradePublisher.publishTrades(matches.getPublicTrades());
        snapshotPublisher.publishSnapshot(snapshot);

        log.debug("Published matched trades {}", matches);
    }

}
