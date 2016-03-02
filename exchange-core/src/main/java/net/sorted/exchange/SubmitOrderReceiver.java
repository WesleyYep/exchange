package net.sorted.exchange;

import java.io.IOException;
import java.util.Optional;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import net.sorted.exchange.dao.OrderIdDao;
import net.sorted.exchange.domain.Order;
import net.sorted.exchange.messages.ExchangeOrder;
import net.sorted.exchange.messages.JsonConverter;
import net.sorted.exchange.orderprocessor.OrderProcessor;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SubmitOrderReceiver {
    private final Channel orderChannel;
    private final String queueName;
    private final OrderProcessor orderProcessor;
    private final OrderIdDao orderIdDao;
    private final JsonConverter jsonConverter;
    private final Consumer consumer;

    private Logger log = LogManager.getLogger(SubmitOrderReceiver.class);

    public SubmitOrderReceiver(Channel orderChannel, String queueName, OrderProcessor orderProcessor,
                               OrderIdDao orderIdDao, JsonConverter jsonConverter) {

        this.orderChannel = orderChannel;
        this.queueName = queueName;
        this.orderProcessor = orderProcessor;
        this.orderIdDao = orderIdDao;
        this.jsonConverter = jsonConverter;


        consumer = new DefaultConsumer(orderChannel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                log.debug("Received '{}'", message);
                try {
                    ExchangeOrder order = jsonConverter.exchangeOrderFromJson(message);
                    processSubmitOrder(order);
                    orderChannel.basicAck(envelope.getDeliveryTag(), false);
                    log.debug("Processed message '{}'", message);
                } catch (Throwable t) {
                    log.info("Error processing message " + message, t);
                    orderChannel.basicNack(envelope.getDeliveryTag(), false, false);
                }
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

    private void processSubmitOrder(ExchangeOrder order) {
        if (order.getInstrument().equals("REJECT")) {
            throw new RuntimeException("REJECTING order");
        }

        Order o = new Order(orderIdDao.getNextOrderId(), Double.parseDouble(order.getPrice()), order.getSide(), order.getQuantity(), order.getInstrument(), order.getClientId());
        orderProcessor.submitOrder(o);
    }
}
