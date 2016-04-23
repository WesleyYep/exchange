package net.sorted.exchange.web;


//import net.sorted.exchange.domain.OrderType;

public class ClientOrder {

    public enum State {
        unsubmitted, open, filled, partial, cancelled
    }


    private long orderId;
    private String clientId;
    private String correlationId;
    private String instrument;
    private long quantity;
    private String price;
    private ClientSide side = ClientSide.BUY;
    private ClientOrderType type;
    private State state;

    public ClientOrder() {

    }

    public ClientOrder(long orderId, String clientId, String correlationId, String instrument, long quantity,
                       String price, ClientSide side, ClientOrderType type, State state) {
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

    public long getOrderId() {
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

    public ClientSide getSide() {
        return side;
    }

    public ClientOrderType getType() {
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

    public void setSide(ClientSide side) {
        this.side = side;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "ExchangeOrderOld{" +
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
