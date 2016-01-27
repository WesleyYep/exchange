package net.sorted.exchange.config;


import java.util.HashMap;
import java.util.Map;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitMqConfig {
    public static final String ORDER_SUBMIT_EXCHANGE_NAME = "submit.order.exchange";
    public static final String ORDER_SUBMIT_QUEUE_NAME = "submit.order";
    public static final String ORDER_SUBMIT_DEAD_CHANNEL_NAME = "submit.order.dead";
    public static final String ORDER_SUBMIT_DEAD_EXCHANGE = "submit.order.dead.exchange";

    private final Channel orderChannel;

    public RabbitMqConfig(String hostname) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(hostname);
        Connection connection = null;
        try {
            connection = factory.newConnection();
            orderChannel = connection.createChannel();

            // Setup dead message exchange
            String deadQueueName = orderChannel.queueDeclare(ORDER_SUBMIT_DEAD_CHANNEL_NAME, false, false, false, null).getQueue();
            orderChannel.exchangeDeclare(ORDER_SUBMIT_DEAD_EXCHANGE, "direct"); // NB fanout important as this is required for ALL dead letters regardless of routing key
            orderChannel.queueBind(deadQueueName, ORDER_SUBMIT_DEAD_EXCHANGE, "");

            orderChannel.exchangeDeclare(ORDER_SUBMIT_EXCHANGE_NAME, "direct");
            Map<String, Object> args = new HashMap<String, Object>();
            args.put("x-dead-letter-exchange", ORDER_SUBMIT_DEAD_EXCHANGE);
            String submitQueueName = orderChannel.queueDeclare(ORDER_SUBMIT_QUEUE_NAME, false, false, false, args).getQueue();
            orderChannel.queueBind(submitQueueName, ORDER_SUBMIT_EXCHANGE_NAME, "");

        } catch (Exception e) {
            throw new RuntimeException("Cannot configure rabbit mq", e);
        }
    }

    public Channel getSubmitOrderChannel() {
        return orderChannel;
    }
}
