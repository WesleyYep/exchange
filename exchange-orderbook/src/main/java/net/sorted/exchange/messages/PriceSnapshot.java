package net.sorted.exchange.messages;


public class PriceSnapshot {
    private final String clientId;
    private final String instrument;
    private final String bid;
    private final String ask;

    public PriceSnapshot(String clientId, String instrument, String bid, String ask) {
        this.clientId = clientId;
        this.instrument = instrument;
        this.bid = bid;
        this.ask = ask;
    }

    public String getClientId() {
        return clientId;
    }

    public String getInstrument() {
        return instrument;
    }

    public String getBid() {
        return bid;
    }

    public String getAsk() {
        return ask;
    }
}
