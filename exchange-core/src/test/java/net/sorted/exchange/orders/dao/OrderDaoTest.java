package net.sorted.exchange.orders.dao;

import net.sorted.exchange.orders.domain.Order;
import net.sorted.exchange.orders.domain.OrderStatus;
import net.sorted.exchange.orders.domain.OrderType;
import net.sorted.exchange.orders.domain.Side;
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
        Order o = new Order(1l, 1.1, Side.BUY, 999, "AMZN", "doug", OrderType.LIMIT, OrderStatus.UNSUBMITTED);
        orderDao.addOrder(o);
        assertEquals(o, orderDao.getOrder(1l));
    }
}
