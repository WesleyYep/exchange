package net.sorted.orderbook;


import java.util.List;
import net.sorted.tradecapture.Trade;

public class MatchedTrades {
    private final List<Trade> aggressorTrades;
    private final List<Trade> passiveTrades;
    private final List<Trade> publicTrades;

    public MatchedTrades(List<Trade> aggressorTrades, List<Trade> passiveTrades, List<Trade> publicTrades) {
        this.aggressorTrades = aggressorTrades;
        this.passiveTrades = passiveTrades;
        this.publicTrades = publicTrades;
    }

    public List<Trade> getAggressorTrades() {
        return aggressorTrades;
    }

    public List<Trade> getPassiveTrades() {
        return passiveTrades;
    }

    public List<Trade> getPublicTrades() {
        return publicTrades;
    }

    public boolean hasMatches() { return aggressorTrades.size() > 0; }
}
