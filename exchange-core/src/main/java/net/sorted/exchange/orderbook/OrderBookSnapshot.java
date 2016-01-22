package net.sorted.exchange.orderbook;


import java.util.List;

public class OrderBookSnapshot {
    private final List<OrderBookLevelSnapshot> buyLevels;
    private final List<OrderBookLevelSnapshot> sellLevels;

    public OrderBookSnapshot(List<OrderBookLevelSnapshot> buyLevels, List<OrderBookLevelSnapshot> sellLevels) {
        this.buyLevels = buyLevels;
        this.sellLevels = sellLevels;
    }

    public List<OrderBookLevelSnapshot> getBuyLevels() {
        return buyLevels;
    }

    public List<OrderBookLevelSnapshot> getSellLevels() {
        return sellLevels;
    }
}
