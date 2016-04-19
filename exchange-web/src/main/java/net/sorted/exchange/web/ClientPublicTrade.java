package net.sorted.exchange.web;

public class ClientPublicTrade {

    private String instrumentId;
    private long quantity;
    private double price;
    private long tradeDateMillisSinceEpoch;


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

    public long getTradeDateMillisSinceEpoch() {
        return tradeDateMillisSinceEpoch;
    }

    public void setTradeDateMillisSinceEpoch(long tradeDateMillisSinceEpoch) {
        this.tradeDateMillisSinceEpoch = tradeDateMillisSinceEpoch;
    }

}
