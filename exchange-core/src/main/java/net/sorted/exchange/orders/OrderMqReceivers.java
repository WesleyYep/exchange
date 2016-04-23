package net.sorted.exchange.orders;


import java.util.ArrayList;
import java.util.List;

public class OrderMqReceivers {
    private List<SubmitOrderReceiver> receivers = new ArrayList<>();

    public void addReceiver(SubmitOrderReceiver submitOrderReceiver) {
        receivers.add(submitOrderReceiver);
    }

    public void startAll() {
        for (SubmitOrderReceiver r : receivers) {
            r.startReceiving();
        }
    }

    public void stopAll() {
        for (SubmitOrderReceiver r : receivers) {
            r.stopReceiving();
        }

    }
}
