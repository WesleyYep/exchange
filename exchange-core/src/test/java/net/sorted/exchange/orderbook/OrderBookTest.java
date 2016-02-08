package net.sorted.exchange.orderbook;

import net.sorted.exchange.domain.Order;
import net.sorted.exchange.domain.Side;
import net.sorted.exchange.TradeIdDaoInMemory;
import net.sorted.exchange.domain.Trade;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static net.sorted.exchange.domain.Side.*;
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
        MatchedTrades matching = orderBook.addOrder(new Order("0", (double) 100.0, BUY, 1000, "USDAUD"));

        assertNotNull(matching);
        assertTrue(matching.hasMatches() == false);

        List<Order> buys = orderBook.getAllOrdersForSide(BUY);
        assertNotNull(buys);
        assertEquals(1, buys.size());

    }

    @Test
    public void testAddManyBuyOrdersSamePrice() {

        orderBook.addOrder(new Order("0", (double)100.0, BUY, 1000, "USDAUD"));
        orderBook.addOrder(new Order("10", (double) 100.0, BUY, 2000, "USDAUD"));
        orderBook.addOrder(new Order("20", (double) 100.0, BUY, 500, "USDAUD"));

        List<Order> bids = orderBook.getAllOrdersForSide(BUY);
        assertNotNull(bids);
        assertEquals(3, bids.size());
        assertEquals("0", bids.get(0).getId());
        assertEquals("10", bids.get(1).getId());
        assertEquals("20", bids.get(2).getId());

    }

    @Test
    public void testAddManyBuyOrdersDifferentPrice() {

        orderBook.addOrder(new Order("0", (double)100.0, BUY, 1000, "USDAUD"));
        orderBook.addOrder(new Order("10", (double) 200.0, BUY, 2000, "USDAUD"));
        orderBook.addOrder(new Order("20", (double) 50.0, BUY, 500, "USDAUD"));

        List<Order> bids = orderBook.getAllOrdersForSide(BUY);
        assertNotNull(bids);
        assertEquals(3, bids.size());
        assertEquals("10", bids.get(0).getId());
        assertEquals("0", bids.get(1).getId());
        assertEquals("20", bids.get(2).getId());
    }

    @Test
    public void testAddManyBuyAndSellOrdersDifferentPrice() {

        orderBook.addOrder(new Order("0", (double)100.0, BUY, 1000, "USDAUD"));
        orderBook.addOrder(new Order("10", (double)200.0, BUY, 2000, "USDAUD"));
        orderBook.addOrder(new Order("20", (double) 50.0, BUY, 500, "USDAUD"));

        orderBook.addOrder(new Order("100", (double) 2000.0, SELL, 1000, "USDAUD"));
        orderBook.addOrder(new Order("101", (double) 3000.0, SELL, 2000, "USDAUD"));
        orderBook.addOrder(new Order("102", (double) 4000.0, SELL, 500, "USDAUD"));
        orderBook.addOrder(new Order("103", (double) 5000.0, SELL, 500, "USDAUD"));

        List<Order> bids = orderBook.getAllOrdersForSide(BUY);
        assertNotNull(bids);
        assertEquals(3, bids.size());
        assertEquals("10", bids.get(0).getId());
        assertEquals("0", bids.get(1).getId());
        assertEquals("20", bids.get(2).getId());

        List<Order> asks = orderBook.getAllOrdersForSide(SELL);
        assertNotNull(asks);
        assertEquals(4, asks.size());
        assertEquals("100", asks.get(0).getId());
        assertEquals("101", asks.get(1).getId());
        assertEquals("102", asks.get(2).getId());
        assertEquals("103", asks.get(3).getId());
    }

    @Test
    public void testPriceAtLevel() {
        orderBook.addOrder(new Order("0", (double)100.0, BUY, 1000, "USDAUD"));
        orderBook.addOrder(new Order("10", (double)200.0, BUY, 2000, "USDAUD"));
        orderBook.addOrder(new Order("20", (double)50.0, BUY, 500, "USDAUD"));

        orderBook.addOrder(new Order("100", (double)300.0, SELL, 1000, "USDAUD"));
        orderBook.addOrder(new Order("101", (double)400.0, SELL, 2000, "USDAUD"));
        orderBook.addOrder(new Order("102", (double) 500.0, SELL, 500, "USDAUD"));
        orderBook.addOrder(new Order("103", (double) 600.0, SELL, 500, "USDAUD"));

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
        orderBook.addOrder(new Order("0", (double)100.0, BUY, 1000, "USDAUD"));
        orderBook.addOrder(new Order("10", (double)200.0, BUY, 2000, "USDAUD"));
        orderBook.addOrder(new Order("20", (double)50.0, BUY, 500, "USDAUD"));

        orderBook.addOrder(new Order("100", (double)2000.0, SELL, 1000, "USDAUD"));
        orderBook.addOrder(new Order("101", (double)3000.0, SELL, 2000, "USDAUD"));
        orderBook.addOrder(new Order("102", (double) 3000.0, SELL, 500, "USDAUD"));
        orderBook.addOrder(new Order("103", (double) 3000.0, SELL, 500, "USDAUD"));

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
        orderBook.addOrder(new Order("0", (double)100.0, BUY, 1000, "USDAUD"));
        orderBook.addOrder(new Order("10", (double) 200.0, BUY, 2000, "USDAUD"));
        orderBook.addOrder(new Order("20", (double) 50.0, BUY, 500, "USDAUD"));

        orderBook.removeOrder("10");
        List<Order> bids = orderBook.getAllOrdersForSide(BUY);
        assertNotNull(bids);
        assertEquals(2, bids.size());
        assertEquals("0", bids.get(0).getId());
        assertEquals("20", bids.get(1).getId());

        assertEquals(1000, orderBook.getSizeAtLevel(BUY, 1));
        assertEquals(500, orderBook.getSizeAtLevel(BUY, 2));

        assertEquals(50.0, orderBook.getPriceAtLevel(BUY, 2), 0.0001);

    }

    @Test
    public void testModifyOrder() {
        orderBook.addOrder(new Order("0", (double) 100.0, BUY, 1000, "USDAUD"));
        orderBook.addOrder(new Order("10", (double) 200.0, BUY, 2000, "USDAUD"));
        orderBook.addOrder(new Order("20", (double) 50.0, BUY, 500, "USDAUD"));

        orderBook.modifyOrder("10", 3000);
        List<Order> bids = orderBook.getAllOrdersForSide(BUY);
        assertNotNull(bids);
        assertEquals(3, bids.size());
        assertEquals("10", bids.get(0).getId());
        assertEquals("0", bids.get(1).getId());
        assertEquals("20", bids.get(2).getId());

        assertEquals(3000, orderBook.getSizeAtLevel(BUY, 1));
        assertEquals(1000, orderBook.getSizeAtLevel(BUY, 2));

        assertEquals(200.0, orderBook.getPriceAtLevel(BUY, 1), 0.0001);
        assertEquals(100.0, orderBook.getPriceAtLevel(BUY, 2), 0.0001);
    }

    @Test
    public void testMatchesSingleTradeBothFilled() {
        MatchedTrades match = orderBook.addOrder(new Order("0", (double) 100.0, BUY, 1000, "USDAUD"));
        assertFalse("Should be no matches", match.hasMatches());

        match = orderBook.addOrder(new Order("1", (double) 100.0, SELL, 1000, "USDAUD"));
        assertNotNull(match);
        assertTrue(match.hasMatches());
        assertNotNull(match.getPassiveTrades());
        assertEquals(1, match.getPassiveTrades().size());
        assertEquals("0", match.getPassiveTrades().get(0).getOrderId());

        assertNotNull(match.getAggressorTrades());
        assertEquals(1, match.getAggressorTrades().size());
        assertEquals("1", match.getAggressorTrades().get(0).getOrderId());

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
        MatchedTrades match = orderBook.addOrder(new Order("0", (double) 100.0, BUY, 10000, "USDAUD"));
        assertFalse("Should be no matches", match.hasMatches());

        match = orderBook.addOrder(new Order("1", (double) 100.0, SELL, 1000, "USDAUD"));
        assertNotNull(match);
        assertTrue(match.hasMatches());
        assertNotNull(match.getPassiveTrades());
        assertEquals(1, match.getPassiveTrades().size());
        assertEquals("0", match.getPassiveTrades().get(0).getOrderId());
        assertEquals(100.0, match.getPassiveTrades().get(0).getPrice(), 0.01);
        assertEquals(1000, match.getPassiveTrades().get(0).getQuantity());

        assertNotNull(match.getAggressorTrades());
        assertEquals(1, match.getAggressorTrades().size());
        assertEquals("1", match.getAggressorTrades().get(0).getOrderId());
        assertEquals(100.0, match.getAggressorTrades().get(0).getPrice(), 0.01);
        assertEquals(1000, match.getAggressorTrades().get(0).getQuantity());

        assertNotNull(match.getPublicTrades());
        assertEquals(1, match.getPublicTrades().size());
        Trade publicTrade = match.getPublicTrades().get(0);
        assertEquals(1000, publicTrade.getQuantity());
        assertEquals(100.0, publicTrade.getPrice(), 0.01);

        assertEquals(1, orderBook.getAllOrdersForSide(BUY).size());
        assertEquals(9000, orderBook.getOrder("0").getQuantity());
        assertEquals(0, orderBook.getAllOrdersForSide(SELL).size());
    }

    @Test
    public void testMultiBuyAndSellTradesFromOrder() {
        orderBook.addOrder(new Order("0", (double) 98.0, SELL, 490, "USDAUD"));
        orderBook.addOrder(new Order("10", (double) 97.0, SELL, 375, "USDAUD"));
        orderBook.addOrder(new Order("20", (double) 96.0, SELL, 100, "USDAUD"));
        orderBook.addOrder(new Order("30", (double) 96.0, SELL, 150, "USDAUD"));

        MatchedTrades match = orderBook.addOrder(new Order("40", (double) 97.0, BUY, 600, "USDAUD"));
        assertNotNull(match);
        List<Trade> passive = match.getPassiveTrades();
        assertNotNull(passive);
        assertEquals(3, passive.size());
        assertEquals("20", passive.get(0).getOrderId());
        assertEquals("30", passive.get(1).getOrderId());
        assertEquals("10", passive.get(2).getOrderId());
        assertEquals(350, passive.get(2).getQuantity());

        List<Trade> aggressor = match.getAggressorTrades();
        assertNotNull(aggressor);
        assertEquals(2, aggressor.size());
        assertEquals("40", aggressor.get(0).getOrderId());
        assertEquals("40", aggressor.get(1).getOrderId());
        assertEquals(350, aggressor.get(1).getQuantity());

        assertEquals(25, orderBook.getSizeAtLevel(Side.SELL, 1));
    }



}
