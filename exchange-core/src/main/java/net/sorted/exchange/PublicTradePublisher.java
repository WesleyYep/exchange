package net.sorted.exchange;


import java.util.List;

public interface PublicTradePublisher {
    void publishTrades(List<Trade> trades);
    void publishTrade(Trade trade);
}
