package net.sorted.exchange.orders.orderbook;


import java.util.List;

public class OrderBookSnapshot {
    private final String instrumentId;
    private final List<OrderBookLevelSnapshot> buyLevels;
    private final List<OrderBookLevelSnapshot> sellLevels;

    public OrderBookSnapshot(String instrumentId, List<OrderBookLevelSnapshot> buyLevels, List<OrderBookLevelSnapshot> sellLevels) {
        this.instrumentId = instrumentId;
        this.buyLevels = buyLevels;
        this.sellLevels = sellLevels;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public List<OrderBookLevelSnapshot> getBuyLevels() {
        return buyLevels;
    }

    public List<OrderBookLevelSnapshot> getSellLevels() {
        return sellLevels;
    }
}
