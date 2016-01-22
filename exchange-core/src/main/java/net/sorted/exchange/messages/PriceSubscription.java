package net.sorted.exchange.messages;


public class PriceSubscription {
    private final String clientId;
    private final String instrument;

    public PriceSubscription(String clientId, String instrument) {
        this.clientId = clientId;
        this.instrument = instrument;
    }

    public String getClientId() {
        return clientId;
    }

    public String getInstrument() {
        return instrument;
    }
}
