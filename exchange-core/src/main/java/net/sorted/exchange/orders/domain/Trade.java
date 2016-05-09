package net.sorted.exchange.orders.domain;


import org.joda.time.DateTime;

public class Trade {
    private final long tradeId;
    private final long orderId;
    private final String clientId;
    private final String instrumentId;
    private final long quantity;
    private final double price;
    private final Side side;
    private final DateTime tradeDate;

    public Trade(long tradeId, long orderId, String clientId, String instrumentId, long quantity, double price, Side side, DateTime tradeDate) {
        this.tradeId = tradeId;
        this.orderId = orderId;
        this.clientId = clientId;
        this.instrumentId = instrumentId;
        this.quantity = quantity;
        this.price = price;
        this.side = side;
        this.tradeDate = tradeDate;
    }

    public long getTradeId() {
        return tradeId;
    }

    public long getOrderId() {
        return orderId;
    }

    public String getClientId() {
        return clientId;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public long getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public Side getSide() {
        return side;
    }

    public DateTime getTradeDate() {
        return tradeDate;
    }

    @Override
    public String toString() {
        return "Trade{" +
                "tradeId='" + tradeId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", clientId='" + clientId + '\'' +
                ", instrumentId='" + instrumentId + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", side=" + side +
                ", tradeDate=" + tradeDate +
                '}';
    }
}
