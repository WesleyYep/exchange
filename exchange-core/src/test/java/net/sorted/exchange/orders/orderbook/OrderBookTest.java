package net.sorted.exchange.orders.orderbook;

import net.sorted.exchange.orders.domain.Order;
import net.sorted.exchange.orders.domain.OrderStatus;
import net.sorted.exchange.orders.domain.OrderType;
import net.sorted.exchange.orders.domain.Side;
import net.sorted.exchange.orders.dao.TradeIdDaoInMemory;
import net.sorted.exchange.orders.domain.Trade;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static net.sorted.exchange.orders.domain.Side.*;
import static org.junit.Assert.*;

public class OrderBookTest {

    private OrderBook orderBook;

    @Before
    public void before() {

        orderBook = new OrderBookInMemory("INSTR", new TradeIdDaoInMemory());
    }


    @Test
    public void testEmptyBook() {


        List<Order> bids = orderBook.getAllOrdersForSide(BUY);
        assertNotNull(bids);
        assertEquals(0, bids.size());

        List<Order> asks = orderBook.getAllOrdersForSide(SELL);
        assertNotNull(asks);
        assertEquals(0, asks.size());
    }

    @Test
    public void testAddSingleBuyOrder() {
        MatchedTrades matching = orderBook.addOrder(new Order(0l, (double) 100.0, BUY, 1000, "AMZN", "client1", OrderType.LIMIT, OrderStatus.UNSUBMITTED));

        assertNotNull(matching);
        assertTrue(matching.hasMatches() == false);

        List<Order> buys = orderBook.getAllOrdersForSide(BUY);
        assertNotNull(buys);
        assertEquals(1, buys.size());

    }

    @Test
    public void testAddManyBuyOrdersSamePrice() {

        orderBook.addOrder(new Order(0l, (double)100.0, BUY, 1000, "AMZN", "client1", OrderType.LIMIT, OrderStatus.UNSUBMITTED));
        orderBook.addOrder(new Order(10l, (double) 100.0, BUY, 2000, "AMZN", "client1", OrderType.LIMIT, OrderStatus.UNSUBMITTED));
        orderBook.addOrder(new Order(20l, (double) 100.0, BUY, 500, "AMZN", "client1", OrderType.LIMIT, OrderStatus.UNSUBMITTED));

        List<Order> bids = orderBook.getAllOrdersForSide(BUY);
        assertNotNull(bids);
        assertEquals(3, bids.size());
        assertEquals(0l, bids.get(0).getId());
        assertEquals(10l, bids.get(1).getId());
        assertEquals(20l, bids.get(2).getId());

    }

    @Test
    public void testAddManyBuyOrdersDifferentPrice() {

        orderBook.addOrder(new Order(0l, (double)100.0, BUY, 1000, "AMZN", "client1", OrderType.LIMIT, OrderStatus.UNSUBMITTED));
        orderBook.addOrder(new Order(10l, (double) 200.0, BUY, 2000, "AMZN", "client1", OrderType.LIMIT, OrderStatus.UNSUBMITTED));
        orderBook.addOrder(new Order(20l, (double) 50.0, BUY, 500, "AMZN", "client1", OrderType.LIMIT, OrderStatus.UNSUBMITTED));

        List<Order> bids = orderBook.getAllOrdersForSide(BUY);
        assertNotNull(bids);
        assertEquals(3, bids.size());
        assertEquals(10l, bids.get(0).getId());
        assertEquals(0l, bids.get(1).getId());
        assertEquals(20l, bids.get(2).getId());
    }

    @Test
    public void testAddManyBuyAndSellOrdersDifferentPrice() {

        orderBook.addOrder(new Order(0l, (double)100.0, BUY, 1000, "AMZN", "client1", OrderType.LIMIT, OrderStatus.UNSUBMITTED));
        orderBook.addOrder(new Order(10l, (double)200.0, BUY, 2000, "AMZN", "client1", OrderType.LIMIT, OrderStatus.UNSUBMITTED));
        orderBook.addOrder(new Order(20l, (double) 50.0, BUY, 500, "AMZN", "client1", OrderType.LIMIT, OrderStatus.UNSUBMITTED));

        orderBook.addOrder(new Order(100l, (double) 2000.0, SELL, 1000, "AMZN", "client1", OrderType.LIMIT, OrderStatus.UNSUBMITTED));
        orderBook.addOrder(new Order(101l, (double) 3000.0, SELL, 2000, "AMZN", "client1", OrderType.LIMIT, OrderStatus.UNSUBMITTED));
        orderBook.addOrder(new Order(102l, (double) 4000.0, SELL, 500, "AMZN", "client1", OrderType.LIMIT, OrderStatus.UNSUBMITTED));
        orderBook.addOrder(new Order(103l, (double) 5000.0, SELL, 500, "AMZN", "client1", OrderType.LIMIT, OrderStatus.UNSUBMITTED));

        List<Order> bids = orderBook.getAllOrdersForSide(BUY);
        assertNotNull(bids);
        assertEquals(3, bids.size());
        assertEquals(10l, bids.get(0).getId());
        assertEquals(0l, bids.get(1).getId());
        assertEquals(20l, bids.get(2).getId());

        List<Order> asks = orderBook.getAllOrdersForSide(SELL);
        assertNotNull(asks);
        assertEquals(4, asks.size());
        assertEquals(100l, asks.get(0).getId());
        assertEquals(101l, asks.get(1).getId());
        assertEquals(102l, asks.get(2).getId());
        assertEquals(103l, asks.get(3).getId());
    }

    @Test
    public void testPriceAtLevel() {
        orderBook.addOrder(new Order(0l, (double)100.0, BUY, 1000, "AMZN", "client1", OrderType.LIMIT, OrderStatus.UNSUBMITTED));
        orderBook.addOrder(new Order(10l, (double)200.0, BUY, 2000, "AMZN", "client1", OrderType.LIMIT, OrderStatus.UNSUBMITTED));
        orderBook.addOrder(new Order(20l, (double)50.0, BUY, 500, "AMZN", "client1", OrderType.LIMIT, OrderStatus.UNSUBMITTED));

        orderBook.addOrder(new Order(100l, (double)300.0, SELL, 1000, "AMZN", "client1", OrderType.LIMIT, OrderStatus.UNSUBMITTED));
        orderBook.addOrder(new Order(101l, (double)400.0, SELL, 2000, "AMZN", "client1", OrderType.LIMIT, OrderStatus.UNSUBMITTED));
        orderBook.addOrder(new Order(102l, (double) 500.0, SELL, 500, "AMZN", "client1", OrderType.LIMIT, OrderStatus.UNSUBMITTED));
        orderBook.addOrder(new Order(103l, (double) 600.0, SELL, 500, "AMZN", "client1", OrderType.LIMIT, OrderStatus.UNSUBMITTED));

        assertEquals(200.0, orderBook.getPriceAtLevel(BUY, 1), 0.0001);
        assertEquals(100.0, orderBook.getPriceAtLevel(BUY, 2), 0.0001);
        assertEquals(50.0, orderBook.getPriceAtLevel(BUY, 3), 0.0001);

        assertEquals(300, orderBook.getPriceAtLevel(SELL, 1), 0.0001);
        assertEquals(400.0, orderBook.getPriceAtLevel(SELL, 2), 0.0001);
        assertEquals(500.0, orderBook.getPriceAtLevel(SELL, 3), 0.0001);
        assertEquals(600.0, orderBook.getPriceAtLevel(SELL, 4), 0.0001);

        assertEquals(Double.MAX_VALUE, orderBook.getPriceAtLevel(SELL, 99), 0.0001);
        assertEquals(0.0, orderBook.getPriceAtLevel(BUY, 99), 0.0001);

    }

    @Test
    public void testSizeAtLevel() {
        orderBook.addOrder(new Order(0l, (double)100.0, BUY, 1000, "AMZN", "client1", OrderType.LIMIT, OrderStatus.UNSUBMITTED));
        orderBook.addOrder(new Order(10l, (double)200.0, BUY, 2000, "AMZN", "client1", OrderType.LIMIT, OrderStatus.UNSUBMITTED));
        orderBook.addOrder(new Order(20l, (double)50.0, BUY, 500, "AMZN", "client1", OrderType.LIMIT, OrderStatus.UNSUBMITTED));

        orderBook.addOrder(new Order(100l, (double)2000.0, SELL, 1000, "AMZN", "client1", OrderType.LIMIT, OrderStatus.UNSUBMITTED));
        orderBook.addOrder(new Order(101l, (double)3000.0, SELL, 2000, "AMZN", "client1", OrderType.LIMIT, OrderStatus.UNSUBMITTED));
        orderBook.addOrder(new Order(102l, (double) 3000.0, SELL, 500, "AMZN", "client1", OrderType.LIMIT, OrderStatus.UNSUBMITTED));
        orderBook.addOrder(new Order(103l, (double) 3000.0, SELL, 500, "AMZN", "client1", OrderType.LIMIT, OrderStatus.UNSUBMITTED));

        assertEquals(2000, orderBook.getSizeAtLevel(BUY, 1));
        assertEquals(1000, orderBook.getSizeAtLevel(BUY, 2));
        assertEquals(500, orderBook.getSizeAtLevel(BUY, 3));

        assertEquals(1000, orderBook.getSizeAtLevel(SELL, 1));
        assertEquals(3000, orderBook.getSizeAtLevel(SELL, 2));


        assertEquals(0, orderBook.getSizeAtLevel(SELL, 99));
        assertEquals(0, orderBook.getSizeAtLevel(BUY, 99));

    }

    @Test
    public void testRemoveOrder() {
        orderBook.addOrder(new Order(0l, (double)100.0, BUY, 1000, "AMZN", "client1", OrderType.LIMIT, OrderStatus.UNSUBMITTED));
        orderBook.addOrder(new Order(10l, (double) 200.0, BUY, 2000, "AMZN", "client1", OrderType.LIMIT, OrderStatus.UNSUBMITTED));
        orderBook.addOrder(new Order(20l, (double) 50.0, BUY, 500, "AMZN", "client1", OrderType.LIMIT, OrderStatus.UNSUBMITTED));

        orderBook.removeOrder(10l);
        List<Order> bids = orderBook.getAllOrdersForSide(BUY);
        assertNotNull(bids);
        assertEquals(2, bids.size());
        assertEquals(0l, bids.get(0).getId());
        assertEquals(20l, bids.get(1).getId());

        assertEquals(1000, orderBook.getSizeAtLevel(BUY, 1));
        assertEquals(500, orderBook.getSizeAtLevel(BUY, 2));

        assertEquals(50.0, orderBook.getPriceAtLevel(BUY, 2), 0.0001);

    }

    @Test
    public void testMatchesSingleOrderBothFilled() {
        MatchedTrades match = orderBook.addOrder(new Order(0l, (double) 100.0, BUY, 1000, "AMZN", "client1", OrderType.LIMIT, OrderStatus.UNSUBMITTED));
        assertFalse("Should be no matches", match.hasMatches());

        match = orderBook.addOrder(new Order(1l, (double) 100.0, SELL, 1000, "AMZN", "client1", OrderType.LIMIT, OrderStatus.UNSUBMITTED));
        assertNotNull(match);
        assertTrue(match.hasMatches());

        assertEquals(2, match.getFills().size());

        assertNotNull(match.getPassiveTrades());
        assertEquals(1, match.getPassiveTrades().size());
        assertEquals(0l, match.getPassiveTrades().get(0).getOrderId());

        assertNotNull(match.getAggressorTrades());
        assertEquals(1, match.getAggressorTrades().size());
        assertEquals(1l, match.getAggressorTrades().get(0).getOrderId());

        assertNotNull(match.getPublicTrades());
        assertEquals(1, match.getPublicTrades().size());
        Trade publicTrade = match.getPublicTrades().get(0);
        assertEquals(1000, publicTrade.getQuantity());
        assertEquals(100.0, publicTrade.getPrice(), 0.01);

        assertEquals(0, orderBook.getAllOrdersForSide(BUY).size());
        assertEquals(0, orderBook.getAllOrdersForSide(SELL).size());
    }

    @Test
    public void testMatchesSingleTradeAggressorFilledPassivePartial() {
        MatchedTrades match = orderBook.addOrder(new Order(0l, (double) 100.0, BUY, 10000, "AMZN", "client1", OrderType.LIMIT, OrderStatus.UNSUBMITTED));
        assertFalse("Should be no matches", match.hasMatches());

        match = orderBook.addOrder(new Order(1l, (double) 100.0, SELL, 1000, "AMZN", "client1", OrderType.LIMIT, OrderStatus.UNSUBMITTED));
        assertNotNull(match);
        assertTrue(match.hasMatches());

        assertEquals(2, match.getFills().size());

        assertNotNull(match.getPassiveTrades());
        assertEquals(1, match.getPassiveTrades().size());
        assertEquals(0l, match.getPassiveTrades().get(0).getOrderId());
        assertEquals(100.0, match.getPassiveTrades().get(0).getPrice(), 0.01);
        assertEquals(1000, match.getPassiveTrades().get(0).getQuantity());

        assertNotNull(match.getAggressorTrades());
        assertEquals(1, match.getAggressorTrades().size());
        assertEquals(1l, match.getAggressorTrades().get(0).getOrderId());
        assertEquals(100.0, match.getAggressorTrades().get(0).getPrice(), 0.01);
        assertEquals(1000, match.getAggressorTrades().get(0).getQuantity());

        assertNotNull(match.getPublicTrades());
        assertEquals(1, match.getPublicTrades().size());
        Trade publicTrade = match.getPublicTrades().get(0);
        assertEquals(1000, publicTrade.getQuantity());
        assertEquals(100.0, publicTrade.getPrice(), 0.01);

        assertEquals(1, orderBook.getAllOrdersForSide(BUY).size());
        assertEquals(9000, orderBook.getOrder(0l).getUnfilledQuantity());
        assertEquals(0, orderBook.getAllOrdersForSide(SELL).size());
    }

    @Test
    public void testAggressorPartialFill() {
        orderBook.addOrder(new Order(0l, 98.0, SELL, 100, "AMZN", "client1", OrderType.LIMIT, OrderStatus.UNSUBMITTED));
        MatchedTrades match = orderBook.addOrder(new Order(10l, 98.0, BUY, 1000, "AMZN", "client2", OrderType.LIMIT, OrderStatus.UNSUBMITTED));

        assertNotNull(match);
        assertTrue(match.hasMatches());
        assertEquals("2 fills for each match", 2, match.getFills().size());

        List<Order> sellOrders = orderBook.getAllOrdersForSide(SELL);
        assertEquals(0, sellOrders.size());

        List<Order> buyOrders = orderBook.getAllOrdersForSide(BUY);
        assertEquals(1, buyOrders.size());
        assertEquals(900, buyOrders.get(0).getUnfilledQuantity());
    }

    @Test
    public void testMultiBuyAndSellTradesFromOrder() {
        orderBook.addOrder(new Order(0l, 98.0, SELL, 490, "AMZN", "client1", OrderType.LIMIT, OrderStatus.UNSUBMITTED));
        orderBook.addOrder(new Order(10l, 97.0, SELL, 375, "AMZN", "client1", OrderType.LIMIT, OrderStatus.UNSUBMITTED));
        orderBook.addOrder(new Order(20l, 96.0, SELL, 100, "AMZN", "client1", OrderType.LIMIT, OrderStatus.UNSUBMITTED));
        orderBook.addOrder(new Order(30l, 96.0, SELL, 150, "AMZN", "client1", OrderType.LIMIT, OrderStatus.UNSUBMITTED));

        MatchedTrades match = orderBook.addOrder(new Order(40l, (double) 97.0, BUY, 600, "AMZN", "client1", OrderType.LIMIT, OrderStatus.UNSUBMITTED));
        assertNotNull(match);
        assertTrue(match.hasMatches());

        assertEquals("2 fills for each match", 6, match.getFills().size());

        assertEquals(600, match.getFills().stream().filter(f -> f.getOrderId() == 40l).mapToLong(f -> f.getQuantity()).sum());
        assertEquals("Should be 3 fills for the BUY", 3, match.getFills().stream().filter(f -> f.getOrderId() == 40l).count());

        assertEquals(150, match.getFills().stream().filter(f -> f.getOrderId() == 30l).mapToLong(f -> f.getQuantity()).sum());
        assertEquals(100, match.getFills().stream().filter(f -> f.getOrderId() == 20l).mapToLong(f -> f.getQuantity()).sum());
        assertEquals(350, match.getFills().stream().filter(f -> f.getOrderId() == 10l).mapToLong(f -> f.getQuantity()).sum());


        List<Trade> passive = match.getPassiveTrades();
        assertNotNull(passive);
        assertEquals(3, passive.size());
        assertEquals(20l, passive.get(0).getOrderId());
        assertEquals(30l, passive.get(1).getOrderId());
        assertEquals(10l, passive.get(2).getOrderId());
        assertEquals(350, passive.get(2).getQuantity());

        List<Trade> aggressor = match.getAggressorTrades();
        assertNotNull(aggressor);
        assertEquals(2, aggressor.size());
        assertEquals(40l, aggressor.get(0).getOrderId());
        assertEquals(40l, aggressor.get(1).getOrderId());
        assertEquals(350, aggressor.get(1).getQuantity());

        assertEquals(25, orderBook.getSizeAtLevel(Side.SELL, 1));
    }

}
