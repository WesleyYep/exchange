package net.sorted.exchange.dao;

import net.sorted.exchange.domain.Order;
import net.sorted.exchange.domain.Side;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OrderDaoTest {
    private OrderDao orderDao;

    @Before
    public void before() {
        orderDao = new OrderDaoInMemory();
    }


    @Test
    public void testAddAndRetrieve() {
        //String id, double price, Side side, long quantity, String symbol, String clientId) {
        Order o = new Order("a1", 1.1, Side.BUY, 999, "AMZN", "doug");
        orderDao.addOrder(o);
        assertEquals(o, orderDao.getOrder("a1"));
    }
}
