package net.sorted.exchange.web;


//import net.sorted.exchange.domain.OrderType;

public class ClientOrder {

    public enum State {
        unsubmitted, open, filled, partial, cancelled, rejected
    }


    private long orderId;
    private long clientId;
    private String instrument;
    private long quantity;
    private long unfilled;
    private String price;
    private ClientSide side = ClientSide.BUY;
    private ClientOrderType type;
    private State state;

    public ClientOrder() {

    }

    public ClientOrder(long orderId, long clientId, String instrument, long quantity, long unfilled,
                       String price, ClientSide side, ClientOrderType type, State state) {
        this.orderId = orderId;
        this.clientId = clientId;
        this.instrument = instrument;
        this.quantity = quantity;
        this.unfilled = unfilled;
        this.price = price;
        this.side = side;
        this.type = type;
        this.state = state;
    }

    public long getOrderId() {
        return orderId;
    }

    public long getClientId() {
        return clientId;
    }

    public String getInstrument() {
        return instrument;
    }

    public long getQuantity() {
        return quantity;
    }

    public String getPrice() {
        return price;
    }

    public ClientSide getSide() {
        return side;
    }

    public ClientOrderType getType() {
        return type;
    }

    public State getState() {
        return state;
    }

    public long getUnfilled() {
        return unfilled;
    }

    public void setUnfilled(long unfilled) {
        this.unfilled = unfilled;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setSide(ClientSide side) {
        this.side = side;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "ClientOrder{" +
                "orderId=" + orderId +
                ", clientId=" + clientId +
                ", instrument='" + instrument + '\'' +
                ", quantity=" + quantity +
                ", unfilled=" + unfilled +
                ", price='" + price + '\'' +
                ", side=" + side +
                ", type=" + type +
                ", state=" + state +
                '}';
    }
}
