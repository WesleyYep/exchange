package net.sorted.tradecapture;

import net.sorted.orderbook.Order;

public interface TradeCapture {
    Trade addOrder(Order order) ;
}
