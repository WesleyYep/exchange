package net.sorted.exchange.orderbook;


public class OrderBookLevelSnapshot {
    private final double price;
    private final long volume;

    public OrderBookLevelSnapshot(double price, long volume) {
        this.price = price;
        this.volume = volume;
    }

    public double getPrice() {
        return price;
    }

    public long getVolume() {
        return volume;
    }
}
