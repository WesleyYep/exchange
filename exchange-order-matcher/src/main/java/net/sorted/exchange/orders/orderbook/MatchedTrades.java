package net.sorted.exchange.orders.orderbook;


import java.util.List;
import net.sorted.exchange.orders.domain.Order;
import net.sorted.exchange.orders.domain.OrderFill;
import net.sorted.exchange.orders.domain.Trade;

public class MatchedTrades {
    private final List<Trade> aggressorTrades;
    private final List<Trade> passiveTrades;
    private final List<Trade> publicTrades;
    private final List<OrderFill> fills;
    private final List<Order> updatedOrders;

    public MatchedTrades(List<Trade> aggressorTrades, List<Trade> passiveTrades, List<Trade> publicTrades, List<OrderFill> fills, List<Order> updatedOrders) {
        this.aggressorTrades = aggressorTrades;
        this.passiveTrades = passiveTrades;
        this.publicTrades = publicTrades;
        this.fills = fills;
        this.updatedOrders = updatedOrders;
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

    public List<OrderFill> getFills() {
        return fills;
    }

    public List<Order> getUpdatedOrders() {
        return updatedOrders;
    }

    public boolean hasMatches() { return aggressorTrades.size() > 0; }

    @Override
    public String toString() {
        return "MatchedTrades{" +
                "aggressorTrades=" + aggressorTrades +
                ", passiveTrades=" + passiveTrades +
                ", publicTrades=" + publicTrades +
                ", fills=" + fills +
                ", updatedOrders=" + updatedOrders +
                '}';
    }
}
