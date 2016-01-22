package net.sorted.exchange;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Optional;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import net.sorted.exchange.messages.ExchangeOrder;
import net.sorted.exchange.messages.JsonConverter;
import net.sorted.exchange.orderprocessor.OrderProcessor;

public class SubmitOrderReceiver {
    private final Channel orderChannel;
    private final String queueName;
    private final OrderProcessorLocator orderProcessorLocator;
    private final OrderIdDao orderIdDao;
    private final JsonConverter jsonConverter;
    private final Consumer consumer;

    public SubmitOrderReceiver(Channel orderChannel, String queueName, OrderProcessorLocator orderProcessorLocator,
                               OrderIdDao orderIdDao, JsonConverter jsonConverter) {

        this.orderChannel = orderChannel;
        this.queueName = queueName;
        this.orderProcessorLocator = orderProcessorLocator;
        this.orderIdDao = orderIdDao;
        this.jsonConverter = jsonConverter;

        try {
            orderChannel.basicQos(1);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot set SubmitOrder queue to only send one message at a time");
        }

        consumer = new DefaultConsumer(orderChannel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [x] Received '" + message + "'");
                try {
                    ExchangeOrder order = jsonConverter.exchangeOrderFromJson(message);
                    processSubmitOrder(order);
                } catch (Throwable t) {
                    System.out.println("Rejecting because "+t.getMessage());
                    t.printStackTrace();
                    orderChannel.basicReject(envelope.getDeliveryTag(), true);
                } finally {
                    orderChannel.basicAck(envelope.getDeliveryTag(), false);
                    System.out.println("Processed message");
                }
            }
        };

    }

    public void startReceiving() {
        try {
            orderChannel.basicConsume(queueName, false, consumer);
        } catch (IOException e) {
            throw new RuntimeException("Error consuming message ", e);
        }
    }

    private void processSubmitOrder(ExchangeOrder order) {
        Optional<OrderProcessor> processor = orderProcessorLocator.getProcessor(order.getInstrument());
        if (processor.isPresent()) {
            Order o = new Order(orderIdDao.getNextOrderId(), Double.parseDouble(order.getPrice()), order.getSide(), order.getQuantity(), order.getInstrument());
            processor.get().submitOrder(o);
        } else {
            throw new RuntimeException("No processor for instrument "+order.getInstrument());
        }
    }
}
