package net.sorted.exchange.orders;

import java.io.IOException;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import net.sorted.exchange.messages.ExchangeMessage;
import net.sorted.exchange.orders.domain.OrderStatus;
import net.sorted.exchange.orders.domain.OrderType;
import net.sorted.exchange.orders.domain.Side;
import net.sorted.exchange.orders.orderprocessor.OrderProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SubmitOrderReceiver {
    private final Channel orderChannel;
    private final String queueName;
    private final OrderProcessor orderProcessor;
    private final Consumer consumer;

    private Logger log = LogManager.getLogger(SubmitOrderReceiver.class);

    public SubmitOrderReceiver(Channel orderChannel, String queueName, OrderProcessor orderProcessor) {

        this.orderChannel = orderChannel;
        this.queueName = queueName;
        this.orderProcessor = orderProcessor;

        consumer = new DefaultConsumer(orderChannel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                handleOrderMessage(consumerTag, envelope, properties, body);
            }
        };

        log.info("Ready to process orders");
    }

    public void startReceiving() {

        try {
            orderChannel.basicConsume(queueName, false, consumer);
            log.info("Started consuming messages from {} ", queueName);
        } catch (IOException e) {
            log.info("Error receiving submit order messages. Listener stopping.", e);
            throw new RuntimeException("Error consuming message ", e);
        }

    }

    public void stopReceiving() {
        try {
            orderChannel.basicCancel(queueName);
            log.info("Stopped consuming messages from {} ", queueName);
        } catch (IOException e) {
            throw new RuntimeException("Error stopping listening for order messages", e);
        }
    }

    private void handleOrderMessage(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        ExchangeMessage.Order order = ExchangeMessage.Order.parseFrom(body);

        try {
            processSubmitOrder(order);
            orderChannel.basicAck(envelope.getDeliveryTag(), false);
            log.debug("Processed message '{}'", order);
        } catch (Throwable t) {
            log.info("Error processing message " + order, t);
            orderChannel.basicNack(envelope.getDeliveryTag(), false, false);
        }
    }

    private void processSubmitOrder(ExchangeMessage.Order order) {
        Side side = (order.getSide() == ExchangeMessage.Side.BUY) ? Side.BUY : Side.SELL;


        orderProcessor.submitOrder(Double.parseDouble(order.getPrice()),
                side,
                order.getQuantity(),
                order.getInstrument(),
                order.getClientId(),
                getOrderTypeFromMessage(order.getOrderType()),
                order.getSubmitter());
    }

    private OrderType getOrderTypeFromMessage(ExchangeMessage.Order.OrderType type) {

        switch (type) {
        case LIMIT:
            return OrderType.LIMIT;
        case KILL_OR_FILL:
            return OrderType.KILL_OR_FILL;
        }

        throw new RuntimeException("Unknown OrderType in message (" + type + ") - cannot process message");
    }

    private OrderStatus getOrderStatusFromMessage(ExchangeMessage.Order.State state) {

        switch (state) {
        case UNSUBMITTED:
            return OrderStatus.UNSUBMITTED;
        case OPEN:
            return OrderStatus.OPEN;
        case FILLED:
            return OrderStatus.FILLED;
        case CANCELLED:
            return OrderStatus.CANCELLED;
        case REJECTED:
            return OrderStatus.REJECTED;
        default:
            throw new RuntimeException("Unknow state " + state + " - cannot map to an OrderStatus enum");
        }
    }
}
