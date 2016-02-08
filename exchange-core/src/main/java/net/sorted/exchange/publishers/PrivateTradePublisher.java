package net.sorted.exchange.publishers;


import java.util.List;
import net.sorted.exchange.domain.Trade;

public interface PrivateTradePublisher {
    void publishTrades(List<Trade> trades);
    void publishTrade(Trade trade);
}
