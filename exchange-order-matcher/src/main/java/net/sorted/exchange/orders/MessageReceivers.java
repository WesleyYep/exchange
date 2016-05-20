package net.sorted.exchange.orders;


import java.util.ArrayList;
import java.util.List;
import net.sorted.exchange.orders.msghandler.MessageReceiver;
import net.sorted.exchange.orders.msghandler.SubmitOrderReceiver;

public class MessageReceivers {
    private List<MessageReceiver> receivers = new ArrayList<>();

    public void addReceiver(MessageReceiver submitOrderReceiver) {
        receivers.add(submitOrderReceiver);
    }

    public void startAll() {
        for (MessageReceiver r : receivers) {
            r.startReceiving();
        }
    }

    public void stopAll() {
        for (MessageReceiver r : receivers) {
            r.stopReceiving();
        }

    }
}
