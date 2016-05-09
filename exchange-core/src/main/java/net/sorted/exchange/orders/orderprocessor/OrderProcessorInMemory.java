package net.sorted.exchange.orders.orderprocessor;


import java.util.concurrent.Executor;
import net.sorted.exchange.orders.dao.OrderDao;
import net.sorted.exchange.orders.domain.Order;
import net.sorted.exchange.orders.domain.OrderStatus;
import net.sorted.exchange.orders.domain.OrderType;
import net.sorted.exchange.orders.domain.Side;
import net.sorted.exchange.orders.orderbook.MatchedTrades;
import net.sorted.exchange.orders.orderbook.OrderBook;
import net.sorted.exchange.orders.orderbook.OrderBookSnapshot;
import net.sorted.exchange.orders.publishers.OrderSnapshotPublisher;
import net.sorted.exchange.orders.publishers.PrivateTradePublisher;
import net.sorted.exchange.orders.publishers.PublicTradePublisher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OrderProcessorInMemory implements OrderProcessor {

    private Logger log = LogManager.getLogger(OrderProcessorInMemory.class);

    private final OrderBook orderBook;
    private final OrderDao orderDao;
    private final PrivateTradePublisher privateTradePublisher;
    private final PublicTradePublisher publicTradePublisher;
    private final OrderSnapshotPublisher snapshotPublisher;

    private final Executor publishExecutor;


    private final Object lock = new Object();

    public OrderProcessorInMemory(OrderBook orderBook,
                                  OrderDao orderDao,
                                  PrivateTradePublisher privateTradePublisher,
                                  PublicTradePublisher publicTradePublisher,
                                  OrderSnapshotPublisher snapshotPublisher,
                                  Executor publishExecutor) {
        this.orderBook = orderBook;
        this.orderDao = orderDao;
        this.privateTradePublisher = privateTradePublisher;
        this.publicTradePublisher = publicTradePublisher;
        this.snapshotPublisher = snapshotPublisher;

        this.publishExecutor = publishExecutor;
    }


    @Override
    public long submitOrder(double price, Side side, long quantity, String symbol, String clientId, OrderType type) {
        long orderId = orderDao.getNextOrderId();
        Order order = new Order(orderId, price, side, quantity, symbol, clientId, type, OrderStatus.OPEN);

        MatchedTrades matches;
        OrderBookSnapshot snapshot;
        synchronized (lock) {
            matches = orderBook.addOrder(order);
            snapshot = orderBook.getSnapshot();
        }

        publishResultInBackground(matches, snapshot);

        return orderId;
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
