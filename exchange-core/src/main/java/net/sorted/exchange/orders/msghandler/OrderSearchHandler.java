package net.sorted.exchange.orders.msghandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import net.sorted.exchange.messages.ExchangeMessage;
import net.sorted.exchange.orders.domain.Order;
import net.sorted.exchange.orders.orderbook.OrderBook;
import net.sorted.exchange.orders.orderbook.OrderBookSnapshot;
import net.sorted.exchange.orders.publishers.DomainWithMessageConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OrderSearchHandler implements MessageReceiver {

    private final Channel channel;
    private final String queueName;
    private final Consumer consumer;
    private final Map<String, OrderBook> instrumentIdToOrderBook;

    private Logger log = LogManager.getLogger(OrderSearchHandler.class);


    public OrderSearchHandler(Channel channel, String queueName, Map<String, OrderBook> instrumentIdToOrderBook) {
        this.channel = channel;
        this.queueName = queueName;
        this.instrumentIdToOrderBook = instrumentIdToOrderBook;

        consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                handleOrderSearchMessage(consumerTag, envelope, properties, body);
            }
        };

        log.info("Ready to process orders");
    }

    @Override
    public void startReceiving() {

        try {
            channel.basicConsume(queueName, false, consumer);
            log.info("Started consuming order search messages from {} ", queueName);
        } catch (IOException e) {
            log.info("Error receiving order search messages. Listener stopping.", e);
            throw new RuntimeException("Error consuming message ", e);
        }

    }

    @Override
    public void stopReceiving() {
        try {
            channel.basicCancel(queueName);
            log.info("Stopped consuming messages from {} ", queueName);
        } catch (IOException e) {
            throw new RuntimeException("Error stopping listening for order messages", e);
        }
    }

    private void handleOrderSearchMessage(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

        String correlationId = properties.getCorrelationId();

        ExchangeMessage.OrderSearch request = ExchangeMessage.OrderSearch.parseFrom(body);

        try {
            List<Order> orders = processOrderSearch(request);

            AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(correlationId)
                    .build();

            ExchangeMessage.OrderSearchResults message = DomainWithMessageConverter.domainOrderListToProtobufMessage(orders);
            channel.basicPublish("", properties.getReplyTo(), replyProps, message.toByteArray());

            channel.basicAck(envelope.getDeliveryTag(), false);

            log.debug("Processed message '{}'", request);
        } catch (Throwable t) {
            log.info("Error processing message " + request, t);
            channel.basicNack(envelope.getDeliveryTag(), false, false);
        }
    }

    private List<Order> processOrderSearch(ExchangeMessage.OrderSearch request) {

        log.debug("Processing order search search");

        return new ArrayList<>();
    }

}
