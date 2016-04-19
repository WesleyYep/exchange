package net.sorted.exchange.web;

import java.util.List;

public class ClientOrderSnapshot {
    private String instrumentId;
    private List<ClientSnapshotLevel> buy;
    private List<ClientSnapshotLevel> sell;

    public String getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId;
    }

    public List<ClientSnapshotLevel> getBuy() {
        return buy;
    }

    public void setBuy(List<ClientSnapshotLevel> buy) {
        this.buy = buy;
    }

    public List<ClientSnapshotLevel> getSell() {
        return sell;
    }

    public void setSell(List<ClientSnapshotLevel> sell) {
        this.sell = sell;
    }
}
