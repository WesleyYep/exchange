package net.sorted.exchange;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
