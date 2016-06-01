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
import net.sorted.exchange.orders.publishers.OrderUpdatePublisher;
import net.sorted.exchange.orders.publishers.PrivateTradePublisher;
import net.sorted.exchange.orders.publishers.PublicTradePublisher;
import net.sorted.exchange.orders.repository.OrderRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OrderProcessorDb implements OrderProcessor {

    private Logger log = LogManager.getLogger(OrderProcessorDb.class);

    private final OrderBook orderBook;
    private final OrderRepository orderRepository;
    private final PrivateTradePublisher privateTradePublisher;
    private final PublicTradePublisher publicTradePublisher;
    private final OrderSnapshotPublisher snapshotPublisher;
    private final OrderUpdatePublisher orderUpdatePublisher;

    private final Executor publishExecutor;
    private final OrderFillService orderFillService;

    private final Object lock = new Object();


    public OrderProcessorDb(OrderBook orderBook,
                            OrderRepository orderRepository,
                            PrivateTradePublisher privateTradePublisher,
                            PublicTradePublisher publicTradePublisher,
                            OrderSnapshotPublisher snapshotPublisher,
                            OrderUpdatePublisher orderUpdatePublisher,
                            Executor publishExecutor,
                            OrderFillService orderFillService) {
        this.orderBook = orderBook;
        this.orderRepository = orderRepository;
        this.privateTradePublisher = privateTradePublisher;
        this.publicTradePublisher = publicTradePublisher;
        this.snapshotPublisher = snapshotPublisher;
        this.orderUpdatePublisher = orderUpdatePublisher;
        this.publishExecutor = publishExecutor;

        this.orderFillService = orderFillService;

        inflateOrderBook();
    }

    @Override
    public long submitOrder(double price, Side side, long quantity, String symbol, long clientId, OrderType type, String orderSubmitter, long submittedMs) {

        Order order = orderRepository.save(new Order(-1, price, side, quantity, symbol, clientId, type, OrderStatus.OPEN, orderSubmitter, submittedMs));
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

        Order cancelled = new Order(order.getId(), order.getPrice(), order.getSide(), order.getQuantity(), order.getUnfilledQuantity(), order.getInstrumentId(),
                                    order.getClientId(), order.getType(), OrderStatus.CANCELLED, order.getOrderSubmitter(), order.getSubmittedMs());
        orderRepository.save(cancelled);
        orderUpdatePublisher.publishUpdate(cancelled);
        snapshotPublisher.publishSnapshot(snapshot);
    }

    @Override
    public OrderBookSnapshot getSnapshot() {
        synchronized (lock) {
            return orderBook.getSnapshot();
        }
    }

    private void publishResultInBackground(final MatchedTrades matches, final OrderBookSnapshot snapshot) {
        publishExecutor.execute(() -> publishResult(matches, snapshot));
    }

    private void publishResult(MatchedTrades matches, OrderBookSnapshot snapshot) {

        List<OrderFill> fills = matches.getFills();

        if (fills.isEmpty() == false) {
            // Write the fills as a single transaction
            orderFillService.saveAll(fills);
        }

        // Send out the messages
        privateTradePublisher.publishTrades(matches.getAggressorTrades());
        privateTradePublisher.publishTrades(matches.getPassiveTrades());
        publicTradePublisher.publishTrades(matches.getPublicTrades());
        snapshotPublisher.publishSnapshot(snapshot);
        orderUpdatePublisher.publishUpdates(matches.getUpdatedOrders());

        log.debug("Published matched trades {}", matches);
    }

    private void inflateOrderBook() {
        List<Order> orders = orderRepository.findUnfilledByInstrumentId(orderBook.getInstrumentId());
        for (Order order : orders) {
            OrderStatus status = order.getStatus();
            if (status == OrderStatus.OPEN || status == OrderStatus.PARTIAL_FILL) {
                orderBook.addOrder(order);
            }
        }
    }
}
