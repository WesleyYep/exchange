package net.sorted.exchange.orders.msghandler;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import net.sorted.exchange.messages.ExchangeMessage;
import net.sorted.exchange.orders.orderbook.OrderBook;
import net.sorted.exchange.orders.orderbook.OrderBookSnapshot;
import net.sorted.exchange.orders.orderprocessor.OrderProcessor;
import net.sorted.exchange.orders.publishers.DomainWithMessageConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OrderBookSnapshotRequestHandler implements MessageReceiver {

    private final Channel channel;
    private final String queueName;
    private final Consumer consumer;
    private final OrderProcessor orderProcessor;

    private Logger log = LogManager.getLogger(OrderBookSnapshotRequestHandler.class);


    public OrderBookSnapshotRequestHandler(Channel channel, String queueName, OrderProcessor orderProcessor) {
        this.channel = channel;
        this.queueName = queueName;
        this.orderProcessor = orderProcessor;

        consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                handleOrderBookSnapshotRequestMessage(consumerTag, envelope, properties, body);
            }
        };

        log.info("Ready to process orders");
    }

    @Override
    public void startReceiving() {

        try {
            channel.basicConsume(queueName, false, consumer);
            log.info("Started consuming snapshot request messages from {} ", queueName);
        } catch (IOException e) {
            log.info("Error receiving snapshot request messages. Listener stopping.", e);
            throw new RuntimeException("Error consuming snapshot request message ", e);
        }

    }

    @Override
    public void stopReceiving() {
        try {
            channel.basicCancel(queueName);
            log.info("Stopped consuming snapshot request messages from {} ", queueName);
        } catch (IOException e) {
            throw new RuntimeException("Error stopping listening for snapshot request  messages", e);
        }
    }

    private void handleOrderBookSnapshotRequestMessage(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

        String correlationId = properties.getCorrelationId();

        ExchangeMessage.OrderBookSnapshotRequest request = ExchangeMessage.OrderBookSnapshotRequest.parseFrom(body);

        try {
            OrderBookSnapshot snapshot = processOrderBookSnapshotRequest();

            AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(correlationId)
                    .build();

            ExchangeMessage.OrderBookSnapshot message = DomainWithMessageConverter.domainSnapshotToProtobufMessage(snapshot);
            channel.basicPublish("", properties.getReplyTo(), replyProps, message.toByteArray());

            channel.basicAck(envelope.getDeliveryTag(), false);

            log.debug("Processed message '{}'", request);
        } catch (Throwable t) {
            log.info("Error processing message " + request, t);
            channel.basicNack(envelope.getDeliveryTag(), false, false);
        }
    }

    private OrderBookSnapshot processOrderBookSnapshotRequest() {
        return orderProcessor.getSnapshot();
    }
}
