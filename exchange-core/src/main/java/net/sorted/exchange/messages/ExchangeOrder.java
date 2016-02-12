package net.sorted.exchange.messages;


import net.sorted.exchange.domain.OrderType;
import net.sorted.exchange.domain.Side;

public class ExchangeOrder {

    public enum State {
        unsubmitted, open, filled, partial, cancelled
    }


    private String orderId;
    private String clientId;
    private String correlationId;
    private String instrument;
    private long quantity;
    private String price;
    private Side side = Side.BUY;
    private OrderType type;
    private State state;

    public ExchangeOrder() {

    }

    public ExchangeOrder(String orderId, String clientId, String correlationId, String instrument, long quantity,
                         String price, Side side, OrderType type, State state) {
        this.orderId = orderId;
        this.clientId = clientId;
        this.correlationId = correlationId;
        this.instrument = instrument;
        this.quantity = quantity;
        this.price = price;
        this.side = side;
        this.type = type;
        this.state = state;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getClientId() {
        return clientId;
    }

    public String getCorrelationId() {
        return correlationId;
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

    public Side getSide() {
        return side;
    }

    public OrderType getType() {
        return type;
    }

    public State getState() {
        return state;
    }


    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setSide(Side side) {
        this.side = side;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "ExchangeOrder{" +
                "orderId='" + orderId + '\'' +
                ", clientId='" + clientId + '\'' +
                ", correlationId='" + correlationId + '\'' +
                ", instrument='" + instrument + '\'' +
                ", quantity=" + quantity +
                ", price='" + price + '\'' +
                ", side=" + side +
                ", type=" + type +
                ", state=" + state +
                '}';
    }
}
