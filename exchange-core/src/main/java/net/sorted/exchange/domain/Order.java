package net.sorted.exchange.domain;

public class Order {

    private String id;
    private OrderType type;
    private double price;
    private Side side;
    private long quantity;
    private String symbol;
    private String clientId;


    public Order(String id, double price, Side side, long quantity, String symbol, String clientId) {
        this.id = id;
        this.price = price;
        this.quantity = quantity;
        this.side = side;
        this.symbol = symbol;
        this.clientId = clientId;
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

    public String getClientId() {
        return clientId;
    }
}
