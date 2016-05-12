package net.sorted.exchange.orders.orderprocessor;


import java.util.List;
import java.util.concurrent.Executor;
import net.sorted.exchange.orders.domain.Order;
import net.sorted.exchange.orders.domain.OrderFill;
import net.sorted.exchange.orders.domain.OrderStatus;
import net.sorted.exchange.orders.domain.OrderType;
import net.sorted.exchange.orders.domain.Side;
import net.sorted.exchange.orders.orderbook.MatchedTrades;
import net.sorted.exchange.orders.orderbook.OrderBook;
import net.sorted.exchange.orders.orderbook.OrderBookSnapshot;
import net.sorted.exchange.orders.publishers.OrderSnapshotPublisher;
import net.sorted.exchange.orders.publishers.PrivateTradePublisher;
import net.sorted.exchange.orders.publishers.PublicTradePublisher;
import net.sorted.exchange.orders.repository.OrderFillRepository;
import net.sorted.exchange.orders.repository.OrderRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

public class OrderProcessorDb implements OrderProcessor {

    private Logger log = LogManager.getLogger(OrderProcessorDb.class);

    private final OrderBook orderBook;
    private final OrderRepository orderRepository;
    private final OrderFillRepository orderFillRepository;
    private final PrivateTradePublisher privateTradePublisher;
    private final PublicTradePublisher publicTradePublisher;
    private final OrderSnapshotPublisher snapshotPublisher;

    private final Executor publishExecutor;
    private final OrderFillService orderFillService;

    private final Object lock = new Object();


    public OrderProcessorDb(OrderBook orderBook,
                            OrderRepository orderRepository,
                            OrderFillRepository orderFillRepository,
                            PrivateTradePublisher privateTradePublisher,
                            PublicTradePublisher publicTradePublisher,
                            OrderSnapshotPublisher snapshotPublisher,
                            Executor publishExecutor,
                            OrderFillService orderFillService) {
        this.orderBook = orderBook;
        this.orderRepository = orderRepository;
        this.orderFillRepository = orderFillRepository;
        this.privateTradePublisher = privateTradePublisher;
        this.publicTradePublisher = publicTradePublisher;
        this.snapshotPublisher = snapshotPublisher;

        this.publishExecutor = publishExecutor;

        this.orderFillService = orderFillService;

        inflateOrderBook();
    }

    @Override
    public long submitOrder(double price, Side side, long quantity, String symbol, String clientId, OrderType type) {

        Order order = orderRepository.save(new Order(-1, price, side, quantity, symbol, clientId, type, OrderStatus.OPEN));
        order.setUnfilledQuantity(quantity);

        MatchedTrades matches;
        OrderBookSnapshot snapshot;
        synchronized (lock) {
            matches = orderBook.addOrder(order);
            snapshot = orderBook.getSnapshot();
        }

        publishResultInBackground(matches, snapshot);

        return order.getId();
    }


    @Override
    public void cancelOrder(Order order) {

        OrderBookSnapshot snapshot;
        synchronized (lock) {
            orderBook.removeOrder(order.getId());
            snapshot = orderBook.getSnapshot();
        }

        Order cancelled = new Order(order.getId(), order.getPrice(), order.getSide(), order.getQuantity(), order.getUnfilledQuantity(), order.getInstrumentId(), order.getClientId(), order.getType(), OrderStatus.CANCELLED);
        orderRepository.save(cancelled);

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
        publishExecutor.execute(() -> publishResult(matches, snapshot));
    }

    private void publishResult(MatchedTrades matches, OrderBookSnapshot snapshot) {

        // Write the fills as a single transaction
        orderFillService.saveAll(matches.getFills());

        // Send out the messages
        privateTradePublisher.publishTrades(matches.getAggressorTrades());
        privateTradePublisher.publishTrades(matches.getPassiveTrades());
        publicTradePublisher.publishTrades(matches.getPublicTrades());
        snapshotPublisher.publishSnapshot(snapshot);

        log.debug("Published matched trades {}", matches);
    }

    private void inflateOrderBook() {
        List<Order> orders = orderRepository.findByInstrumentId(orderBook.getInstrumentId());
        for (Order order : orders) {
            List<OrderFill> fills = orderFillRepository.findByOrderId(order.getId());
            long filled = fills.stream().mapToLong(f -> f.getQuantity()).sum();
            order.setUnfilledQuantity(order.getQuantity() - filled);
            orderBook.addOrder(order);
        }
    }
}
