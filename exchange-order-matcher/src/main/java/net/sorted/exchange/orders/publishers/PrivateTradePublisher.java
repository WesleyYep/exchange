package net.sorted.exchange.orders.publishers;


import java.util.List;
import net.sorted.exchange.orders.domain.Trade;

public interface PrivateTradePublisher {
    void publishTrades(List<Trade> trades);
    void publishTrade(Trade trade);
}
