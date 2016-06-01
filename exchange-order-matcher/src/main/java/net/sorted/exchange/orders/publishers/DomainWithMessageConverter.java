package net.sorted.exchange.orders.publishers;

import java.util.List;
import net.sorted.exchange.messages.ExchangeMessage;
import net.sorted.exchange.orders.domain.Order;
import net.sorted.exchange.orders.domain.OrderStatus;
import net.sorted.exchange.orders.domain.OrderType;
import net.sorted.exchange.orders.domain.Side;
import net.sorted.exchange.orders.domain.Trade;
import net.sorted.exchange.orders.orderbook.OrderBookLevelSnapshot;
import net.sorted.exchange.orders.orderbook.OrderBookSnapshot;
import org.joda.time.DateTime;

public class DomainWithMessageConverter {

    public static ExchangeMessage.OrderBookSnapshot domainSnapshotToProtobufMessage(OrderBookSnapshot snapshot) {
        ExchangeMessage.OrderBookSnapshot.Builder msg = ExchangeMessage.OrderBookSnapshot.newBuilder();

        msg.setInstrumentId(snapshot.getInstrumentId());
        for (OrderBookLevelSnapshot level : snapshot.getBuyLevels()) {
            ExchangeMessage.OrderBookLevel.Builder l = ExchangeMessage.OrderBookLevel.newBuilder();
            l.setPrice(level.getPrice());
            l.setQuantity(level.getQuantity());
            msg.addBuys(l.build());
        }

        for (OrderBookLevelSnapshot level : snapshot.getSellLevels()) {
            ExchangeMessage.OrderBookLevel.Builder l = ExchangeMessage.OrderBookLevel.newBuilder();
            l.setPrice(level.getPrice());
            l.setQuantity(level.getQuantity());
            msg.addSells(l.build());
        }

        return msg.build();
    }

    public static ExchangeMessage.Order domainOrderToProtobufMessage(Order order) {
        ExchangeMessage.Order.Builder msg = ExchangeMessage.Order.newBuilder();

        msg.setClientId(order.getClientId());
        msg.setInstrument(order.getInstrumentId());
        msg.setOrderId(order.getId());
        msg.setOrderType(domainOrderTypeToProtobuf(order.getType()));
        msg.setPrice(order.getPrice() + "");
        msg.setQuantity(order.getQuantity());
        msg.setUnfilled(order.getUnfilledQuantity());
        msg.setSide((order.getSide() == Side.BUY) ? ExchangeMessage.Side.BUY : ExchangeMessage.Side.SELL);
        msg.setState(domaintOrderStateToProtobuf(order.getStatus()));
        msg.setSubmitter(order.getOrderSubmitter());

        return msg.build();
    }

    public static ExchangeMessage.OrderSearchResults domainOrderListToProtobufMessage(List<Order> orders) {
        ExchangeMessage.OrderSearchResults.Builder msg = ExchangeMessage.OrderSearchResults.newBuilder();

        for (Order o : orders) {
            msg.addOrders(domainOrderToProtobufMessage(o));
        }

        return msg.build();
    }

    public static ExchangeMessage.PrivateTrade domainTradeToProtobufMessage(Trade trade) {

        ExchangeMessage.PrivateTrade.Builder msg = ExchangeMessage.PrivateTrade.newBuilder();

        msg.setTradeId(trade.getTradeId());
        msg.setInstrumentId(trade.getInstrumentId().trim());
        msg.setQuantity(trade.getQuantity());
        msg.setPrice(trade.getPrice());
        msg.setSide((trade.getSide() == Side.BUY) ? ExchangeMessage.Side.BUY : ExchangeMessage.Side.SELL);
        msg.setClientId(trade.getClientId());
        msg.setOrderSubmitter(trade.getOrderSubmitter());

        DateTime tradeDate = trade.getTradeDate();
        if (tradeDate != null) {
            msg.setTradeDateMillisSinceEpoch(tradeDate.getMillis());
        } else {
            msg.clearTradeDateMillisSinceEpoch();
        }

        Long orderId = trade.getOrderId();
        if (orderId != null) {
            msg.setOrderId(orderId);
        } else {
            msg.clearOrderId();
        }

        return msg.build();
    }

    private static ExchangeMessage.Order.OrderType domainOrderTypeToProtobuf(OrderType type) {
        if (type == OrderType.LIMIT) {
            return ExchangeMessage.Order.OrderType.LIMIT;
        } else {
            return ExchangeMessage.Order.OrderType.KILL_OR_FILL;
        }
    }

    private static ExchangeMessage.OrderState domaintOrderStateToProtobuf(OrderStatus state) {
        switch (state) {
        case OPEN:
            return ExchangeMessage.OrderState.OPEN;
        case FILLED:
            return ExchangeMessage.OrderState.FILLED;
        case REJECTED:
            return ExchangeMessage.OrderState.REJECTED;
        case CANCELLED:
            return ExchangeMessage.OrderState.CANCELLED;
        case PARTIAL_FILL:
            return ExchangeMessage.OrderState.PARTIAL_FILL;
        case UNSUBMITTED:
        default:
            return ExchangeMessage.OrderState.UNSUBMITTED;
        }
    }

}
