package net.sorted.exchange;


import java.util.List;

public interface PrivateTradePublisher {
    void publishTrades(List<Trade> trades);
    void publishTrade(Trade trade);
}
