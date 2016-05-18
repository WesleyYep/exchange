package net.sorted.exchange.orders.publishers;

import net.sorted.exchange.messages.ExchangeMessage;
import net.sorted.exchange.orders.orderbook.OrderBookLevelSnapshot;
import net.sorted.exchange.orders.orderbook.OrderBookSnapshot;

public class OrderBookSnapshotConverter {
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
}
