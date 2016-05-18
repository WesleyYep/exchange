package net.sorted.exchange.web;

import java.util.ArrayList;
import java.util.List;
import net.sorted.exchange.messages.ExchangeMessage;

public class SnapshotConverter {

    public static ClientOrderSnapshot messageToClient(ExchangeMessage.OrderBookSnapshot message) {
        String instrumentId = message.getInstrumentId();

        ClientOrderSnapshot s = new ClientOrderSnapshot();
        s.setInstrumentId(instrumentId);
        List<ClientSnapshotLevel> buy = new ArrayList<>();
        for (ExchangeMessage.OrderBookLevel l : message.getBuysList()) {
            buy.add(new ClientSnapshotLevel(l.getPrice(), l.getQuantity()));
        }

        List<ClientSnapshotLevel> sell = new ArrayList<>();
        for (ExchangeMessage.OrderBookLevel l : message.getSellsList()) {
            sell.add(new ClientSnapshotLevel(l.getPrice(), l.getQuantity()));
        }

        s.setBuy(buy);
        s.setSell(sell);

        return s;
    }
}
