package net.sorted.exchange.orders.orderbook;


import java.util.List;
import net.sorted.exchange.orders.domain.Order;
import net.sorted.exchange.orders.domain.Trade;

public class MatchedTrades {
    private final List<Trade> aggressorTrades;
    private final List<Trade> passiveTrades;
    private final List<Trade> publicTrades;
    private final List<Order> partialFills;
    private final List<Order> fills;

    public MatchedTrades(List<Trade> aggressorTrades, List<Trade> passiveTrades, List<Trade> publicTrades, List<Order> partialFills, List<Order> fills) {
        this.aggressorTrades = aggressorTrades;
        this.passiveTrades = passiveTrades;
        this.publicTrades = publicTrades;
        this.partialFills = partialFills;
        this.fills = fills;
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

    public List<Order> getPartialFills() {
        return partialFills;
    }

    public List<Order> getFills() {
        return fills;
    }

    public boolean hasMatches() { return aggressorTrades.size() > 0; }

    @Override
    public String toString() {
        return "MatchedTrades{" +
                "aggressorTrades=" + aggressorTrades +
                ", passiveTrades=" + passiveTrades +
                ", publicTrades=" + publicTrades +
                '}';
    }
}
