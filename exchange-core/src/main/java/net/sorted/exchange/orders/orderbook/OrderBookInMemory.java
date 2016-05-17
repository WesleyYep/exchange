package net.sorted.exchange.orders.orderbook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sorted.exchange.orders.domain.Order;
import net.sorted.exchange.orders.domain.OrderFill;
import net.sorted.exchange.orders.domain.Side;
import net.sorted.exchange.orders.dao.TradeIdDao;
import net.sorted.exchange.orders.domain.Trade;
import org.joda.time.DateTime;

import static net.sorted.exchange.orders.domain.Side.*;

public class OrderBookInMemory implements OrderBook {

    private final String instrumentId;
    private final TradeIdDao tradeIdDao;

    private OrdersForSide buyOrders = new OrdersForSide(true);
    private OrdersForSide sellOrders = new OrdersForSide(false);

    private Map<Long, OrdersForSide> orderIdToOrdersForSide = new HashMap<>();


    public OrderBookInMemory(String instrumentId, TradeIdDao tradeIdDao) {
        this.instrumentId = instrumentId;
        this.tradeIdDao = tradeIdDao;
    }

    @Override
    public MatchedTrades addOrder(Order order) {
        Side side = order.getSide();
        OrdersForSide orders = getOrdersForSide(side);
        orders.addOrder(order);
        orderIdToOrdersForSide.put(order.getId(), orders);

        return getTradesMatching(order);
    }

    @Override
    public void removeOrder(Long orderId) {
        OrdersForSide orders = orderIdToOrdersForSide.get(orderId);
        orders.removeOrder(orderId);
    }

    @Override
    public double getPriceAtLevel(Side side, int level) {
        OrdersForSide orders = getOrdersForSide(side);
        return orders.getPriceAtLevel(level);
    }

    @Override
    public long getSizeAtLevel(Side side, int level) {
        OrdersForSide orders = getOrdersForSide(side);
        return orders.getSizeAtLevel(level);
    }

    @Override
    public List<Order> getAllOrdersForSide(Side side) {

        OrdersForSide orders = getOrdersForSide(side);
        return orders.getOrdersByLevelAndTime();
    }

    @Override
    public Order getOrder(Long orderId) {
        return orderIdToOrdersForSide.get(orderId).getOrder(orderId);
    }

    @Override
    public OrderBookSnapshot getSnapshot() {
        List<OrderBookLevelSnapshot> buys = getSnapshotForSide(buyOrders);
        List<OrderBookLevelSnapshot> sells = getSnapshotForSide(sellOrders);

        return new OrderBookSnapshot(instrumentId, buys, sells);
    }

    @Override
    public String getInstrumentId() {
        return instrumentId;
    }

    private List<OrderBookLevelSnapshot> getSnapshotForSide(OrdersForSide orders) {
        int level = 1;
        List<OrderBookLevelSnapshot> snapshots = new ArrayList<>();
        while ((orders.getOrdersAtLevel(level)).size() > 0) {
            OrderBookLevelSnapshot levelSnapshot = new OrderBookLevelSnapshot(orders.getPriceAtLevel(level), orders.getSizeAtLevel(level));
            snapshots.add(levelSnapshot);
            level++;
        }

        return snapshots;
    }

    private MatchedTrades getTradesMatching(Order newOrder) {
        List<Trade> passiveTrades = new ArrayList<>();
        List<Trade> aggressorTrades = new ArrayList<>();
        List<Trade> publicTrades = new ArrayList<>();
        List<Order> filledPassive = new ArrayList<>();
        List<OrderFill> fills = new ArrayList<>();

        long qtyLeftToMatch = newOrder.getUnfilledQuantity();
        boolean stillTradesToMatch = true;

        OrdersForSide ordersForOtherSide = getOrdersForSide(newOrder.getSide().other());

        // go through the levels
        // at each level go through the orders filling the newOrder until it is filled or no more matching orders
        // create a trade for each fill
        // create a public trade for each price level
        int level = 1;
        while (qtyLeftToMatch > 0 && stillTradesToMatch) {
            List<Order> ordersAtLevel = ordersForOtherSide.getOrdersAtLevel(level++);
            if (ordersAtLevel.size() == 0) {
                stillTradesToMatch = false;
                break;
            }

            long qtyTradedAtLevel = 0; // keep track of this for the public feed
            double levelPrice = 0.0;
            for (Order matching : ordersAtLevel) {
                levelPrice = matching.getPrice();
                if (areWeTrading(newOrder, levelPrice) == false) {
                    stillTradesToMatch = false;
                    break;
                }

                long otherOrderQty = matching.getUnfilledQuantity();
                if (otherOrderQty <= qtyLeftToMatch) {
                    // Order can be fully utilised
                    Trade passiveTradeForOrder = getTradeForOrder(matching, otherOrderQty, levelPrice);
                    passiveTrades.add(passiveTradeForOrder);

                    filledPassive.add(matching); // to remove later

                    recordFills(fills, otherOrderQty, levelPrice, newOrder.getId(), matching.getId());

                    OrdersForSide orders = orderIdToOrdersForSide.get(newOrder.getId());
                    orders.partialFill(newOrder.getId(), matching.getId(), levelPrice, otherOrderQty);

                    qtyTradedAtLevel += otherOrderQty;
                    qtyLeftToMatch -= otherOrderQty;

                    if (qtyLeftToMatch <= 0) {
                        break;
                    }
                } else {
                    // matching order can be partially utilised
                    Trade passiveForOrder = getTradeForOrder(matching, qtyLeftToMatch, levelPrice);
                    passiveTrades.add(passiveForOrder);

                    OrdersForSide orders = orderIdToOrdersForSide.get(matching.getId());
                    orders.partialFill(matching.getId(), newOrder.getId(), levelPrice, qtyLeftToMatch);

                    recordFills(fills, qtyLeftToMatch, levelPrice, newOrder.getId(), matching.getId());

                    qtyTradedAtLevel += qtyLeftToMatch;
                    qtyLeftToMatch = 0;
                    break; // once we get to a partial fill, no more matching to be done.
                }
            }

            if (qtyTradedAtLevel > 0) {
                Trade publicTrade = new Trade(-1, -1, -1, newOrder.getInstrumentId(), qtyTradedAtLevel, levelPrice, newOrder.getSide(), new DateTime(), newOrder.getOrderSubmitter());
                Trade aggressorForOrder = getTradeForOrder(newOrder, qtyTradedAtLevel, levelPrice);
                aggressorTrades.add(aggressorForOrder);
                publicTrades.add(publicTrade);
            }
        }


        // Now remove all the filled orders from the book
        for (Order passive : filledPassive) {
            ordersForOtherSide.removeOrder(passive.getId());
        }

        // Remove aggressor order if it is filled
        if (qtyLeftToMatch == 0) {
            getOrdersForSide(newOrder.getSide()).removeOrder(newOrder.getId());
        }

        return new MatchedTrades(aggressorTrades, passiveTrades, publicTrades, fills);
    }

    // When there is a mathc, there isa fill for the aggressor and a matching one for the passive order.
    // They are the same except they are are related to their respective orders with a reference to the other order.
    private void recordFills(List<OrderFill> fills, long qty, double price, long aggressorOrderId, long passiveOrderId) {
        OrderFill passiveFill = new OrderFill(-1, qty, price, passiveOrderId, aggressorOrderId);
        OrderFill aggressorFill = new OrderFill(-1, qty, price, aggressorOrderId, passiveOrderId);
        fills.add(passiveFill);
        fills.add(aggressorFill);
    }


    private boolean areWeTrading(Order newOrder, double levelPrice) {
        if (newOrder.getSide() == BUY) {
            if (levelPrice > newOrder.getPrice()) { // if buying, then other sides price must be same or lower
                return false;
            }
        } else {
            if (levelPrice < newOrder.getPrice()) { // if selling, then other sides price must be same or higher
                return false;
            }
        }
        return true;
    }

    private Trade getTradeForOrder(Order o, long qty, double price ) {
        return new Trade(tradeIdDao.getNextTradeId(), o.getId(), o.getClientId(), o.getInstrumentId(), qty, price, o.getSide(), new DateTime(), o.getOrderSubmitter());
    }


    private OrdersForSide getOrdersForSide(Side side) {
        if (side == BUY) {
            return buyOrders;
        } else {
            return sellOrders;
        }
    }
}
