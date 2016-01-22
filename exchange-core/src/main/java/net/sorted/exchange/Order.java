package net.sorted.exchange;

public class Order {

    private String id;
    private OrderType type;
    private double price;
    private Side side;
    private long quantity;
    private String symbol;


    public Order(String id, double price, Side side, long quantity, String symbol) {
        this.id = id;
        this.price = price;
        this.quantity = quantity;
        this.side = side;
        this.symbol = symbol;
    }


    public String getId() {
        return id;
    }

    public double getPrice(){
        return price;
    }

    public long getQuantity(){
        return quantity;
    }

    public String getSymbol(){
        return symbol;
    }

    public Side getSide() {
        return side;
    }
}
