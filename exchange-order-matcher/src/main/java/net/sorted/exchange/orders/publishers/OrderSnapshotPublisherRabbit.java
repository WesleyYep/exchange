package net.sorted.exchange.orders.publishers;

import java.io.IOException;
import com.rabbitmq.client.Channel;
import net.sorted.exchange.messages.ExchangeMessage;
import net.sorted.exchange.orders.orderbook.OrderBookSnapshot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class OrderSnapshotPublisherRabbit implements OrderSnapshotPublisher {

    private final Channel channel;
    private final String exchangeName;

    private Logger log = LogManager.getLogger(OrderSnapshotPublisherRabbit.class);

    public OrderSnapshotPublisherRabbit(Channel channel, String exchangeName) {
        this.channel = channel;
        this.exchangeName = exchangeName;
    }

    @Override
    public void publishSnapshot(OrderBookSnapshot snapshot) {
        try {
            ExchangeMessage.OrderBookSnapshot message = DomainWithMessageConverter.domainSnapshotToProtobufMessage(snapshot);
            channel.basicPublish(exchangeName, snapshot.getInstrumentId(), null, message.toByteArray());
            log.debug("Published snapshot " + message + " to exchange " + exchangeName + " with routingKey " + snapshot.getInstrumentId());
        } catch (IOException e) {
            log.error("Cannot publish snapshot message", e);
            throw new RuntimeException("Error publishing snapshot message to exchange " + exchangeName, e);
        }
    }
}
