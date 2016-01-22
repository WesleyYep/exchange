package net.sorted.orderbook;


public class OrderBookLevelSnapshot {
    private final double price;
    private final double volume;

    public OrderBookLevelSnapshot(double price, double volume) {
        this.price = price;
        this.volume = volume;
    }

    public double getPrice() {
        return price;
    }

    public double getVolume() {
        return volume;
    }
}
