package net.sorted.exchange.web;

public class ClientSnapshotLevel {
    private final double price;
    private final long quantity;

    public ClientSnapshotLevel(double price, long quantity) {
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
