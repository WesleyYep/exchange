package net.sorted.exchange.web.rest;

import java.io.IOException;
import java.security.Principal;
import java.util.Optional;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import net.sorted.exchange.config.RabbitMqConfig;
import net.sorted.exchange.messages.ExchangeMessage;
import net.sorted.exchange.web.ClientOrderSnapshot;
import net.sorted.exchange.web.OrderSnapshotCache;
import net.sorted.exchange.web.SnapshotConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderBookSnapshotService {
    private Logger log = LogManager.getLogger(OrderBookSnapshotService.class);

    private static final long TIMEOUT = 2000l;
    private final OrderSnapshotCache snapshotCache;
    private final Channel orderSnapshotRequestChannel;
    private final String orderSnapshotRequestQueueName;

    private final String replyQueueName;
    private final QueueingConsumer consumer;

    @Autowired
    public OrderBookSnapshotService(OrderSnapshotCache snapshotCache,
                                    @Qualifier("orderSnapshotRequestChannel") Channel orderSnapshotRequestChannel,
                                    @Qualifier("orderSnapshotRequestQueueName") String orderSnapshotRequestQueueName) throws IOException {
        this.snapshotCache = snapshotCache;
        this.orderSnapshotRequestChannel = orderSnapshotRequestChannel;
        this.orderSnapshotRequestQueueName = orderSnapshotRequestQueueName;

        // Setup a queue for responses to the requests made by this service
        replyQueueName = orderSnapshotRequestChannel.queueDeclare().getQueue();
        consumer = new QueueingConsumer(orderSnapshotRequestChannel);
        orderSnapshotRequestChannel.basicConsume(replyQueueName, true, consumer);
    }

    @RequestMapping(value="/snapshot", method = {RequestMethod.GET })
    public ClientOrderSnapshot snapshot(String instrument, Principal principal) {
        log.info("Requesting orders available for instrument {} user {}", instrument, principal.getName());

        ClientOrderSnapshot snapshot=null;
        Optional<ClientOrderSnapshot> o = snapshotCache.getSnapshot(instrument);
        if (o.isPresent()) {
            snapshot = o.get();
        } else {
            snapshot = getSnapshot(instrument);
            snapshotCache.setSnapshot(instrument, snapshot);
        }

        return snapshot;
    }

    private ClientOrderSnapshot getSnapshot(String instrument) {
        String correlationId = java.util.UUID.randomUUID().toString();

        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(correlationId)
                .replyTo(replyQueueName)
                .build();

        ExchangeMessage.OrderBookSnapshotRequest.Builder request = ExchangeMessage.OrderBookSnapshotRequest.newBuilder();
        request.setInstrumentId(instrument);

        ClientOrderSnapshot snapshot;

        try {

            // Send the request
            orderSnapshotRequestChannel.basicPublish("", orderSnapshotRequestQueueName, props, request.build().toByteArray());

            // Wait for the response
            // TODO dont wait forever !!!!
            while (true) {
                QueueingConsumer.Delivery delivery = consumer.nextDelivery(TIMEOUT);
                if (delivery.getProperties().getCorrelationId().equals(correlationId)) {
                    ExchangeMessage.OrderBookSnapshot message = ExchangeMessage.OrderBookSnapshot.parseFrom(delivery.getBody());
                    snapshot = SnapshotConverter.messageToClient(message);

                    break;
                }
            }
        } catch (Exception e) {
            // TODO handle this better
            log.error("Error getting snapshot for "+instrument+" from the backend", e);
            snapshot = new ClientOrderSnapshot(instrument);
        }

        return snapshot;
    }
}
