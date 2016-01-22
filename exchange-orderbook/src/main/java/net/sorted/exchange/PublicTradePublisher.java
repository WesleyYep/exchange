package net.sorted.exchange;


import java.util.List;
import net.sorted.tradecapture.Trade;

public interface PublicTradePublisher {
    void publishTrades(List<Trade> trades);
    void publishTrade(Trade trade);
}
