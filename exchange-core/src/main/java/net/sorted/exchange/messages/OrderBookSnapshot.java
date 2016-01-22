package net.sorted.exchange.messages;


import java.util.List;

public class OrderBookSnapshot {
    private final String clientId;
    private final String correlationId;
    private final List<ExchangeOrder> buys;
    private final List<ExchangeOrder> sells;

    public OrderBookSnapshot(String clientId, String correlationId, List<ExchangeOrder> buys, List<ExchangeOrder> sells) {
        this.clientId = clientId;
        this.correlationId = correlationId;
        this.buys = buys;
        this.sells = sells;
    }

    public String getClientId() {
        return clientId;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public List<ExchangeOrder> getBuys() {
        return buys;
    }

    public List<ExchangeOrder> getSells() {
        return sells;
    }
}
