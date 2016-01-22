package net.sorted.exchange.config;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitMqConfig {
    public static final String ORDER_SUBMIT_CHANNEL_NAME = "submit_order";

    private final Channel orderChannel;

    public RabbitMqConfig(String hostname) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(hostname);
        Connection connection = null;
        try {
            connection = factory.newConnection();
            orderChannel = connection.createChannel();
            orderChannel.queueDeclare(ORDER_SUBMIT_CHANNEL_NAME, false, false, false, null);

        } catch (Exception e) {
            throw new RuntimeException("Cannot configure rabbit mq", e);
        }
    }

    public Channel getSubmitOrderChannel() {
        return orderChannel;
    }
}
