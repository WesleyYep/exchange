package net.sorted.orderprocessor;


import net.sorted.exchange.OrderIdDao;
import net.sorted.exchange.OrderSnapshotPublisher;
import net.sorted.exchange.PrivateTradePublisher;
import net.sorted.exchange.PublicTradePublisher;
import net.sorted.exchange.messages.ExchangeOrder;
import net.sorted.orderbook.MatchedTrades;
import net.sorted.orderbook.Order;
import net.sorted.orderbook.OrderBook;
import net.sorted.orderbook.OrderBookSnapshot;

public class OrderProcessorInMemory implements OrderProcessor {

    private final OrderBook orderBook;
    private final OrderIdDao orderIdDao;
    private final PrivateTradePublisher privateTradePublisher;
    private final PublicTradePublisher publicTradePublisher;
    private final OrderSnapshotPublisher snapshotPublisher;

    private final Object lock = new Object();

    public OrderProcessorInMemory(OrderBook orderBook,
                                  OrderIdDao orderIdDao,
                                  PrivateTradePublisher privateTradePublisher,
                                  PublicTradePublisher publicTradePublisher,
                                  OrderSnapshotPublisher snapshotPublisher) {
        this.orderBook = orderBook;
        this.orderIdDao = orderIdDao;
        this.privateTradePublisher = privateTradePublisher;
        this.publicTradePublisher = publicTradePublisher;
        this.snapshotPublisher = snapshotPublisher;
    }


    @Override
    public void submitOrder(Order order) {
        String orderId = orderIdDao.getNextOrderId();
        Order n = new Order(orderId, order.getPrice(), order.getSide(), order.getQuantity(), order.getSymbol());

        MatchedTrades matches = null;
        OrderBookSnapshot snapshot = null;
        synchronized (lock) {
            matches = orderBook.addOrder(n);
            snapshot = orderBook.getSnapshot();
        }

        privateTradePublisher.publishTrades(matches.getAggressorTrades());
        privateTradePublisher.publishTrades(matches.getPassiveTrades());
        publicTradePublisher.publishTrades(matches.getPublicTrades());
        snapshotPublisher.publishSnapshot(snapshot);
    }

    @Override
    public void updateOrder(Order order) {

    }

    @Override
    public void cancelOrder(Order order) {

    }


    private Order getOrder(ExchangeOrder exchangeOrder) {
        String orderId = orderIdDao.getNextOrderId();
        Order o = new Order(orderId,
                Double.parseDouble(exchangeOrder.getPrice()),
                exchangeOrder.getSide(),
                exchangeOrder.getQuantity(),
                exchangeOrder.getInstrument());

        return o;
    }
}
