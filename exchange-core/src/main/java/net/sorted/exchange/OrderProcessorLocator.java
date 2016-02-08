package net.sorted.exchange;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.sorted.exchange.orderprocessor.OrderProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OrderProcessorLocator {
    private final Map<String, OrderProcessor> instrumentToProcessor;

    private Logger log = LogManager.getLogger(OrderProcessorLocator.class);

    public OrderProcessorLocator() {
        this.instrumentToProcessor = new HashMap<>();
    }

    public void addOrderProcessor(String instrument, OrderProcessor processor) {
        instrumentToProcessor.put(instrument, processor);
        log.info("Added processor for instrument {}", instrument);
    }

    public Optional<OrderProcessor> getProcessor(String instrument) {
        OrderProcessor processor = instrumentToProcessor.get(instrument);
        return (processor != null) ? Optional.of(processor) : Optional.empty();
    }
}
