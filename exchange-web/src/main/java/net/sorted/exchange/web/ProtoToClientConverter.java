package net.sorted.exchange.web;

import net.sorted.exchange.messages.ExchangeMessage;

public class ProtoToClientConverter {

    public static ClientOrder orderMessageToClientOrder(ExchangeMessage.Order order) {
        ClientOrder.State clientState = null;
        switch (order.getState()) {
        case OPEN:
            clientState = ClientOrder.State.open;
            break;
        case FILLED:

            clientState = ClientOrder.State.filled;
            break;
        case CANCELLED:
            clientState = ClientOrder.State.cancelled;
            break;
        case REJECTED:
            clientState = ClientOrder.State.rejected;
            break;
        case PARTIAL_FILL:
            clientState = ClientOrder.State.partial;
            break;
        case UNSUBMITTED:
        default:
            clientState = ClientOrder.State.unsubmitted;
            break;
        }

        ClientOrderType clientType = null;
        switch (order.getOrderType()) {
        case LIMIT:
            clientType = ClientOrderType.LIMIT;
            break;
        case KILL_OR_FILL:
            clientType = ClientOrderType.KILL_OR_FILL;
            break;
        }


        return new ClientOrder(order.getOrderId(),
                order.getClientId(),
                order.getInstrument(),
                order.getQuantity(),
                order.getUnfilled(),
                order.getPrice(),
                (order.getSide() == ExchangeMessage.Side.BUY) ? ClientSide.BUY : ClientSide.SELL,
                clientType,
                clientState );
    }
}
