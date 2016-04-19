package net.sorted.exchange.web;

public class ClientPrivateTrade {

    private String tradeId;
    private String instrumentId;
    private long quantity;
    private double price;
    private Side side;
    private long tradeDateMillisSinceEpoch;
    private String clientId;
    private String orderId;

    public enum Side {
        BUY, SELL
    }


    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Side getSide() {
        return side;
    }

    public void setSide(Side side) {
        this.side = side;
    }

    public long getTradeDateMillisSinceEpoch() {
        return tradeDateMillisSinceEpoch;
    }

    public void setTradeDateMillisSinceEpoch(long tradeDateMillisSinceEpoch) {
        this.tradeDateMillisSinceEpoch = tradeDateMillisSinceEpoch;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
