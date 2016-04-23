package net.sorted.exchange.orders.orderbook;


public class OrderBookLevelSnapshot {
    private final double price;
    private final long quantity;

    public OrderBookLevelSnapshot(double price, long quantity) {
        this.price = price;
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public long getQuantity() {
        return quantity;
    }
}
