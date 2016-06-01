package net.sorted.exchange.web;

public class ClientOrderSearch {

    private ClientOrder.State state;
    private long clientId= -1;
    private long orderId = -1;
    private String submitter;
    private String instrument;
    private long fromTimestampMillis;
    private long toTimestampMillis;

    public ClientOrder.State getState() {
        return state;
    }

    public void setState(ClientOrder.State state) {
        this.state = state;
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getSubmitter() {
        return submitter;
    }

    public void setSubmitter(String submitter) {
        this.submitter = submitter;
    }

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    public long getFromTimestampMillis() {
        return fromTimestampMillis;
    }

    public void setFromTimestampMillis(long fromTimestampMillis) {
        this.fromTimestampMillis = fromTimestampMillis;
    }

    public long getToTimestampMillis() {
        return toTimestampMillis;
    }

    public void setToTimestampMillis(long toTimestampMillis) {
        this.toTimestampMillis = toTimestampMillis;
    }

    @Override
    public String toString() {
        return "ClientOrderSearch{" +
                "state=" + state +
                ", clientId=" + clientId +
                ", orderId=" + orderId +
                ", submitter='" + submitter + '\'' +
                ", instrument='" + instrument + '\'' +
                ", fromTimestampMillis=" + fromTimestampMillis +
                ", toTimestampMillis=" + toTimestampMillis +
                '}';
    }
}
