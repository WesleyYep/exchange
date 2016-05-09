package net.sorted.exchange.orders.orderprocessor;


import java.util.List;
import java.util.concurrent.Executor;
import net.sorted.exchange.orders.dao.TradeIdDaoInMemory;
import net.sorted.exchange.orders.dao.OrderDao;
import net.sorted.exchange.orders.dao.OrderDaoInMemory;
import net.sorted.exchange.orders.domain.Order;
import net.sorted.exchange.orders.domain.OrderStatus;
import net.sorted.exchange.orders.domain.OrderType;
import net.sorted.exchange.orders.orderbook.OrderBook;
import net.sorted.exchange.orders.orderbook.OrderBookInMemory;
import net.sorted.exchange.orders.orderbook.OrderBookSnapshot;
import net.sorted.exchange.orders.publishers.OrderSnapshotPublisher;
import net.sorted.exchange.orders.publishers.PrivateTradePublisher;
import net.sorted.exchange.orders.publishers.PublicTradePublisher;
import net.sorted.exchange.orders.repository.OrderRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;

import static net.sorted.exchange.orders.domain.Side.BUY;
import static net.sorted.exchange.orders.domain.Side.SELL;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class OrderProcessorTest {

    private OrderProcessor orderProcessor;
    private OrderBook orderBook;
    private PrivateTradePublisher privateTradePublisher;
    private PublicTradePublisher publicTradePublisher;
    private OrderSnapshotPublisher snapshotPublisher;

    @Autowired
    private OrderRepository orderRepository;


    @Before
    public void before() {

        orderBook = new OrderBookInMemory("INSTR", new TradeIdDaoInMemory());
        OrderDao orderDao = new OrderDaoInMemory();
        privateTradePublisher = mock(PrivateTradePublisher.class);
        publicTradePublisher = mock(PublicTradePublisher.class);
        snapshotPublisher = mock(OrderSnapshotPublisher.class);

//        orderProcessor = new OrderProcessorInMemory(orderBook, orderDao, privateTradePublisher, publicTradePublisher, snapshotPublisher, new DirectExecutor());
        orderProcessor = new OrderProcessorInMemory(orderBook, orderDao, privateTradePublisher, publicTradePublisher, snapshotPublisher, new DirectExecutor());
    }

    @Test
    public void testOrderSubmitToEmptyOrderBook() {
        orderProcessor.submitOrder(100.0, BUY, 1000, "USDAUD", "client1", OrderType.LIMIT);

        // No matches with only 1 order in the book ...

        // ... so no private trades (for aggressor or passive)
        ArgumentCaptor<List> privateTradeCaptor = ArgumentCaptor.forClass(List.class);
        verify(privateTradePublisher, times(2)).publishTrades(privateTradeCaptor.capture());
        assertEquals(0, privateTradeCaptor.getAllValues().get(0).size());
        assertEquals(0, privateTradeCaptor.getAllValues().get(1).size());

        // ... so no public trades
        ArgumentCaptor<List> publicTradeCaptor = ArgumentCaptor.forClass(List.class);
        verify(publicTradePublisher, times(1)).publishTrades(publicTradeCaptor.capture());
        assertEquals(0, publicTradeCaptor.getValue().size());

        // But there will be one buy, so level 1 on buy side will have a volume of that trade but there will be nothing on sell side
        ArgumentCaptor<OrderBookSnapshot> snapshotCaptor = ArgumentCaptor.forClass(OrderBookSnapshot.class);
        verify(snapshotPublisher, times(1)).publishSnapshot(snapshotCaptor.capture());
        assertEquals(1, snapshotCaptor.getValue().getBuyLevels().size());
        assertEquals(1000, snapshotCaptor.getValue().getBuyLevels().get(0).getQuantity());
        assertEquals(0, snapshotCaptor.getValue().getSellLevels().size());

    }

    @Test
    public void testOrderSubmitWithMatches() {
        orderProcessor.submitOrder(90.0, BUY, 500, "USDAUD", "client1", OrderType.LIMIT);
        orderProcessor.submitOrder(100.0, BUY, 500, "USDAUD", "client1", OrderType.LIMIT);
        orderProcessor.submitOrder(100.0, BUY, 500, "USDAUD", "client1", OrderType.LIMIT);
        orderProcessor.submitOrder(100.0, SELL, 1000, "USDAUD", "client1", OrderType.LIMIT);

        // 2 BUYs match the aggressor SELL

        // ... so 1 private trades for aggressor and 2 passive trades for buyers
        // NB each submitOrder will cause 2 calls to privateTradePublisher, we are only interested in the last 2 (6 and 7)
        ArgumentCaptor<List> privateTradeCaptor = ArgumentCaptor.forClass(List.class);
        verify(privateTradePublisher, times(8)).publishTrades(privateTradeCaptor.capture());
        assertEquals(1, privateTradeCaptor.getAllValues().get(6).size());
        assertEquals(2, privateTradeCaptor.getAllValues().get(7).size());

        // ... 1 public trade
        ArgumentCaptor<List> publicTradeCaptor = ArgumentCaptor.forClass(List.class);
        verify(publicTradePublisher, times(4)).publishTrades(publicTradeCaptor.capture());
        assertEquals(1, publicTradeCaptor.getAllValues().get(3).size());

        // One Order left in the book as the others have been matched which will be shown in the snapshot
        ArgumentCaptor<OrderBookSnapshot> snapshotCaptor = ArgumentCaptor.forClass(OrderBookSnapshot.class);
        verify(snapshotPublisher, times(4)).publishSnapshot(snapshotCaptor.capture());
        OrderBookSnapshot snapshot = snapshotCaptor.getAllValues().get(3);
        assertEquals(1, snapshot.getBuyLevels().size());
        assertEquals(500, snapshot.getBuyLevels().get(0).getQuantity());
        assertEquals(0, snapshot.getSellLevels().size());

    }

    // Executor that executes on the current thread. In this way, concurrent code is flattened in the tests to a single thread
    private static class DirectExecutor implements Executor {
        public void execute(Runnable r) {
            r.run();
        }
    }

}
