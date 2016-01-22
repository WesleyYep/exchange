package net.sorted.exchange;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.sorted.exchange.orderprocessor.OrderProcessor;

public class OrderProcessorLocator {
    private final Map<String, OrderProcessor> instrumentToProcessor;

    public OrderProcessorLocator() {
        this.instrumentToProcessor = new HashMap<>();
    }

    public void addOrderProcessor(String instrument, OrderProcessor processor) {
        instrumentToProcessor.put(instrument, processor);
    }

    public Optional<OrderProcessor> getProcessor(String instrument) {
        OrderProcessor processor = instrumentToProcessor.get(instrument);
        return (processor != null) ? Optional.of(processor) : Optional.empty();
    }
}
