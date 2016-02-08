package net.sorted.exchange.orderbook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sorted.exchange.domain.Order;
import net.sorted.exchange.domain.Side;
import net.sorted.exchange.TradeIdDao;
import net.sorted.exchange.domain.Trade;
import org.joda.time.DateTime;

import static net.sorted.exchange.domain.Side.*;

public class OrderBookInMemory implements OrderBook {

    private final String instrumentId;
    private final TradeIdDao tradeIdDao;

    private OrdersForSide buyOrders = new OrdersForSide(true);
    private OrdersForSide sellOrders = new OrdersForSide(false);

    private Map<String, OrdersForSide> orderIdToOrdersForSide = new HashMap<>();


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
    public void removeOrder(String orderId) {
        OrdersForSide orders = orderIdToOrdersForSide.get(orderId);
        orders.removeOrder(orderId);
    }

    @Override
    public MatchedTrades modifyOrder(String orderId, long size) {
        OrdersForSide orders = orderIdToOrdersForSide.get(orderId);
        orders.modifyOrder(orderId, size);

        return getTradesMatching(orders.getOrder(orderId));
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
    public Order getOrder(String orderId) {
        return orderIdToOrdersForSide.get(orderId).getOrder(orderId);
    }

    @Override
    public OrderBookSnapshot getSnapshot() {
        List<OrderBookLevelSnapshot> buys = getSnapshotForSide(buyOrders);
        List<OrderBookLevelSnapshot> sells = getSnapshotForSide(sellOrders);

        return new OrderBookSnapshot(instrumentId, buys, sells);
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
        List<Order> filledPassive = new ArrayList<>();
        List<Trade> publicTrades = new ArrayList<>();

        long qtyToMatch = newOrder.getQuantity();
        boolean stillTradesToMatch = true;

        OrdersForSide ordersForOtherSide = getOrdersForSide(newOrder.getSide().other());

        // go through the levels
        // at each level go through the orders filling the newOrder until it is filled or no more matching orders
        // create a trade for each fill
        // create a public trade for each price level
        int level = 1;
        while (qtyToMatch > 0 && stillTradesToMatch) {
            List<Order> ordersAtLevel = ordersForOtherSide.getOrdersAtLevel(level++);
            if (ordersAtLevel.size() == 0) {
                stillTradesToMatch = false;
                break;
            }

            long qtyAtLevel = 0; // keep track of this for the public feed
            double levelPrice = 0.0;
            for (Order o : ordersAtLevel) {
                levelPrice = o.getPrice();
                if (areWeTrading(newOrder, levelPrice) == false) {
                    stillTradesToMatch = false;
                    break;
                }

                long qty = o.getQuantity();
                if (qty <= qtyToMatch) {
                    // Order can be fully utilised
                    Trade passiveForOrder = getTradeForOrder(o, qty, levelPrice);
                    passiveTrades.add(passiveForOrder);

                    filledPassive.add(o); // to remove later

                    qtyAtLevel += qty;
                    qtyToMatch -= qty;

                    if (qtyToMatch <= 0) {
                        break;
                    }
                } else {
                    // order can be partially utilised
                    Trade passiveForOrder = getTradeForOrder(o, qtyToMatch, levelPrice);
                    passiveTrades.add(passiveForOrder);

                    OrdersForSide orders = orderIdToOrdersForSide.get(o.getId());
                    orders.modifyOrder(o.getId(), o.getQuantity() - qtyToMatch);
                    qtyAtLevel += qtyToMatch;
                    qtyToMatch = 0;
                    break; // once we get to a partial fill, no more matching to be done.
                }
            }

            if (qtyAtLevel > 0) {
                Trade publicTrade = new Trade(null, null, newOrder.getSymbol(), qtyAtLevel, levelPrice, newOrder.getSide(), new DateTime());
                Trade aggressorForOrder = getTradeForOrder(newOrder, qtyAtLevel, levelPrice);
                aggressorTrades.add(aggressorForOrder);
                publicTrades.add(publicTrade);
            }
        }


        // Now remove all the filled orders from the book
        for (Order passive : filledPassive) {
            ordersForOtherSide.removeOrder(passive.getId());
        }

        // Remove aggressor order if it is filled
        if (qtyToMatch == 0) {
            getOrdersForSide(newOrder.getSide()).removeOrder(newOrder.getId());
        }

        return new MatchedTrades(aggressorTrades, passiveTrades, publicTrades);
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
        return new Trade(tradeIdDao.getNextTradeId(), o.getId(), o.getSymbol(), qty, price, o.getSide(), new DateTime());
    }



    private OrdersForSide getOrdersForSide(Side side) {
        if (side == BUY) {
            return buyOrders;
        } else {
            return sellOrders;
        }
    }



}
