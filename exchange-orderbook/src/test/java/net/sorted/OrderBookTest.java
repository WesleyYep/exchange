package net.sorted;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static net.sorted.Side.*;
import static org.junit.Assert.*;

public class OrderBookTest {

    private OrderBook orderBook;

    @Before
    public void before() {
        orderBook = new OrderBookImpl();
    }


    @Test
    public void testEmptyBook() {


        List<Order> bids = orderBook.getAllOrdersForSide(BID);
        assertNotNull(bids);
        assertEquals(0, bids.size());

        List<Order> asks = orderBook.getAllOrdersForSide(ASK);
        assertNotNull(asks);
        assertEquals(0, asks.size());
    }

    @Test
    public void testAddSingleBidOrder() {
        orderBook.addOrder(new Order(0, (double) 100.0, BID, 1000, "USDAUD"));

        List<Order> bids = orderBook.getAllOrdersForSide(BID);
        assertNotNull(bids);
        assertEquals(1, bids.size());

    }

    @Test
    public void testAddManyBidOrdersSamePrice() {

        orderBook.addOrder(new Order(0, (double)100.0, BID, 1000, "USDAUD"));
        orderBook.addOrder(new Order(10, (double)100.0, BID, 2000, "USDAUD"));
        orderBook.addOrder(new Order(20, (double)100.0, BID, 500, "USDAUD"));

        List<Order> bids = orderBook.getAllOrdersForSide(BID);
        assertNotNull(bids);
        assertEquals(3, bids.size());
        assertEquals(0, bids.get(0).getId());
        assertEquals(10, bids.get(1).getId());
        assertEquals(20, bids.get(2).getId());

    }

    @Test
    public void testAddManyBidOrdersDifferentPrice() {

        orderBook.addOrder(new Order(0, (double)100.0, BID, 1000, "USDAUD"));
        orderBook.addOrder(new Order(10, (double)200.0, BID, 2000, "USDAUD"));
        orderBook.addOrder(new Order(20, (double)50.0, BID, 500, "USDAUD"));

        List<Order> bids = orderBook.getAllOrdersForSide(BID);
        assertNotNull(bids);
        assertEquals(3, bids.size());
        assertEquals(10, bids.get(0).getId());
        assertEquals(0, bids.get(1).getId());
        assertEquals(20, bids.get(2).getId());
    }

    @Test
    public void testAddManyBidAndAskOrdersDifferentPrice() {

        orderBook.addOrder(new Order(0, (double)100.0, BID, 1000, "USDAUD"));
        orderBook.addOrder(new Order(10, (double)200.0, BID, 2000, "USDAUD"));
        orderBook.addOrder(new Order(20, (double)50.0, BID, 500, "USDAUD"));

        orderBook.addOrder(new Order(100, (double)20.0, ASK, 1000, "USDAUD"));
        orderBook.addOrder(new Order(101, (double)30.0, ASK, 2000, "USDAUD"));
        orderBook.addOrder(new Order(102, (double)40.0, ASK, 500, "USDAUD"));
        orderBook.addOrder(new Order(103, (double)50.0, ASK, 500, "USDAUD"));

        List<Order> bids = orderBook.getAllOrdersForSide(BID);
        assertNotNull(bids);
        assertEquals(3, bids.size());
        assertEquals(10, bids.get(0).getId());
        assertEquals(0, bids.get(1).getId());
        assertEquals(20, bids.get(2).getId());

        List<Order> asks = orderBook.getAllOrdersForSide(ASK);
        assertNotNull(asks);
        assertEquals(4, asks.size());
        assertEquals(100, asks.get(0).getId());
        assertEquals(101, asks.get(1).getId());
        assertEquals(102, asks.get(2).getId());
        assertEquals(103, asks.get(3).getId());
    }

    @Test
    public void testPriceAtLevel() {
        orderBook.addOrder(new Order(0, (double)100.0, BID, 1000, "USDAUD"));
        orderBook.addOrder(new Order(10, (double)200.0, BID, 2000, "USDAUD"));
        orderBook.addOrder(new Order(20, (double)50.0, BID, 500, "USDAUD"));

        orderBook.addOrder(new Order(100, (double)20.0, ASK, 1000, "USDAUD"));
        orderBook.addOrder(new Order(101, (double)30.0, ASK, 2000, "USDAUD"));
        orderBook.addOrder(new Order(102, (double) 40.0, ASK, 500, "USDAUD"));
        orderBook.addOrder(new Order(103, (double) 50.0, ASK, 500, "USDAUD"));

        assertEquals(200.0, orderBook.getPriceAtLevel(BID, 1), 0.0001);
        assertEquals(100.0, orderBook.getPriceAtLevel(BID, 2), 0.0001);
        assertEquals(50.0, orderBook.getPriceAtLevel(BID, 3), 0.0001);

        assertEquals(20.0, orderBook.getPriceAtLevel(ASK, 1), 0.0001);
        assertEquals(30.0, orderBook.getPriceAtLevel(ASK, 2), 0.0001);
        assertEquals(40.0, orderBook.getPriceAtLevel(ASK, 3), 0.0001);
        assertEquals(50.0, orderBook.getPriceAtLevel(ASK, 4), 0.0001);

        assertEquals(Double.MAX_VALUE, orderBook.getPriceAtLevel(ASK, 99), 0.0001);
        assertEquals(0.0, orderBook.getPriceAtLevel(BID, 99), 0.0001);

    }

    @Test
    public void testSizeAtLevel() {
        orderBook.addOrder(new Order(0, (double)100.0, BID, 1000, "USDAUD"));
        orderBook.addOrder(new Order(10, (double)200.0, BID, 2000, "USDAUD"));
        orderBook.addOrder(new Order(20, (double)50.0, BID, 500, "USDAUD"));

        orderBook.addOrder(new Order(100, (double)20.0, ASK, 1000, "USDAUD"));
        orderBook.addOrder(new Order(101, (double)30.0, ASK, 2000, "USDAUD"));
        orderBook.addOrder(new Order(102, (double) 30.0, ASK, 500, "USDAUD"));
        orderBook.addOrder(new Order(103, (double) 30.0, ASK, 500, "USDAUD"));

        assertEquals(2000, orderBook.getSizeAtLevel(BID, 1));
        assertEquals(1000, orderBook.getSizeAtLevel(BID, 2));
        assertEquals(500, orderBook.getSizeAtLevel(BID, 3));

        assertEquals(1000, orderBook.getSizeAtLevel(ASK, 1));
        assertEquals(3000, orderBook.getSizeAtLevel(ASK, 2));


        assertEquals(0, orderBook.getSizeAtLevel(ASK, 99));
        assertEquals(0, orderBook.getSizeAtLevel(BID, 99));

    }

    @Test
    public void testRemoveOrder() {
        orderBook.addOrder(new Order(0, (double)100.0, BID, 1000, "USDAUD"));
        orderBook.addOrder(new Order(10, (double)200.0, BID, 2000, "USDAUD"));
        orderBook.addOrder(new Order(20, (double)50.0, BID, 500, "USDAUD"));

        orderBook.removeOrder(10);
        List<Order> bids = orderBook.getAllOrdersForSide(BID);
        assertNotNull(bids);
        assertEquals(2, bids.size());
        assertEquals(0, bids.get(0).getId());
        assertEquals(20, bids.get(1).getId());

        assertEquals(1000, orderBook.getSizeAtLevel(BID, 1));
        assertEquals(500, orderBook.getSizeAtLevel(BID, 2));

        assertEquals(50.0, orderBook.getPriceAtLevel(BID, 2), 0.0001);

    }

    @Test
    public void testModifyOrder() {
        orderBook.addOrder(new Order(0, (double)100.0, BID, 1000, "USDAUD"));
        orderBook.addOrder(new Order(10, (double)200.0, BID, 2000, "USDAUD"));
        orderBook.addOrder(new Order(20, (double)50.0, BID, 500, "USDAUD"));

        orderBook.modifyOrder(10, 3000);
        List<Order> bids = orderBook.getAllOrdersForSide(BID);
        assertNotNull(bids);
        assertEquals(3, bids.size());
        assertEquals(10, bids.get(0).getId());
        assertEquals(0, bids.get(1).getId());
        assertEquals(20, bids.get(2).getId());

        assertEquals(3000, orderBook.getSizeAtLevel(BID, 1));
        assertEquals(1000, orderBook.getSizeAtLevel(BID, 2));

        assertEquals(200.0, orderBook.getPriceAtLevel(BID, 1), 0.0001);
        assertEquals(100.0, orderBook.getPriceAtLevel(BID, 2), 0.0001);
    }

    @Test
    public void testMatchingAtLevel1() {
        orderBook.addOrder(new Order(10, (double) 200.0, BID, 2000, "USDAUD"));
        orderBook.addOrder(new Order(20, (double) 250.0, BID, 500, "USDAUD"));

        orderBook.addOrder(new Order(100, (double) 250.0, ASK, 1000, "USDAUD"));
        orderBook.addOrder(new Order(101, (double) 300.0, ASK, 2000, "USDAUD"));

        List<Order> matching = orderBook.getMatchingOrdersByPrice();

        assertNotNull(matching);
        assertEquals(1, matching.size());
        assertEquals(100, matching.get(0).getId());
    }

    @Test
    public void testMatchingAtLevel1and2() {
        orderBook.addOrder(new Order(10, (double)200.0, BID, 2000, "USDAUD"));
        orderBook.addOrder(new Order(20, (double)300.0, BID, 500, "USDAUD"));

        orderBook.addOrder(new Order(100, (double)250.0, ASK, 1000, "USDAUD"));
        orderBook.addOrder(new Order(101, (double) 300.0, ASK, 2000, "USDAUD"));
        orderBook.addOrder(new Order(101, (double) 350.0, ASK, 2000, "USDAUD"));

        List<Order> matching = orderBook.getMatchingOrdersByPrice();

        assertNotNull(matching);
        assertEquals(2, matching.size());
        assertEquals(100, matching.get(0).getId());
        assertEquals(101, matching.get(1).getId());
    }

    @Test
    public void testNothingMatching() {
        orderBook.addOrder(new Order(10, (double)200.0, BID, 2000, "USDAUD"));
        orderBook.addOrder(new Order(20, (double)300.0, BID, 500, "USDAUD"));

        orderBook.addOrder(new Order(100, (double)350.0, ASK, 1000, "USDAUD"));
        orderBook.addOrder(new Order(101, (double) 400.0, ASK, 2000, "USDAUD"));
        orderBook.addOrder(new Order(101, (double) 450.0, ASK, 2000, "USDAUD"));

        List<Order> matching = orderBook.getMatchingOrdersByPrice();

        assertNotNull(matching);
        assertEquals(0, matching.size());

    }
}
