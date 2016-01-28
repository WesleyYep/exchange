package net.sorted.exchange;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import com.rabbitmq.client.Channel;
import net.sorted.exchange.messages.JsonConverter;
import net.sorted.exchange.orderbook.OrderBookSnapshot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class OrderSnapshotPublisherRabbit implements OrderSnapshotPublisher {

    private final Channel channel;
    private final String exchangeName;
    private final JsonConverter converter;

    private Logger log = LogManager.getLogger(OrderSnapshotPublisherRabbit.class);

    public OrderSnapshotPublisherRabbit(Channel channel, String exchangeName, JsonConverter converter) {
        this.channel = channel;
        this.exchangeName = exchangeName;
        this.converter = converter;
    }

    @Override
    public void publishSnapshot(OrderBookSnapshot snapshot) {
        String message = converter.snapshotToJson(snapshot);
        try {
            byte[] bytes = message.getBytes("UTF-8");
            channel.basicPublish(exchangeName, snapshot.getInstrumentId(), null, bytes);
            log.debug("Published snapshot " + message);
        } catch (UnsupportedEncodingException e) {
            // This should never be able to happen!!!
            log.error("Cannot convert snapshot message " + snapshot + " to json", e);
            throw new RuntimeException("Cannot convert snapshot message to json " + snapshot, e);
        } catch (IOException e) {
            log.error("Cannot publish snapshot message", e);
            throw new RuntimeException("Error publishing snapshot message to exchange " + exchangeName, e);
        }
    }
}
