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

    public static final String PUBLIC_TRADE_EXCHANGE_NAME = "public.trade.exchange";

    public static final String SNAPSHOT_EXCHANGE_NAME = "snapshot.exchange";


    private final Channel orderChannel;
    private final Channel publicTradeChannel;
    private final Channel snapshotChannel;

    public RabbitMqConfig(String hostname) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(hostname);
        Connection connection = null;
        try {
            connection = factory.newConnection();
            orderChannel = connection.createChannel();

            // Setup Submit Order with dead message exchange
            String deadQueueName = orderChannel.queueDeclare(ORDER_SUBMIT_DEAD_CHANNEL_NAME, false, false, false, null).getQueue();
            orderChannel.exchangeDeclare(ORDER_SUBMIT_DEAD_EXCHANGE, "direct");
            orderChannel.queueBind(deadQueueName, ORDER_SUBMIT_DEAD_EXCHANGE, "");

            orderChannel.exchangeDeclare(ORDER_SUBMIT_EXCHANGE_NAME, "direct");
            Map<String, Object> args = new HashMap<String, Object>();
            args.put("x-dead-letter-exchange", ORDER_SUBMIT_DEAD_EXCHANGE);
            String submitQueueName = orderChannel.queueDeclare(ORDER_SUBMIT_QUEUE_NAME, false, false, false, args).getQueue();
            orderChannel.queueBind(submitQueueName, ORDER_SUBMIT_EXCHANGE_NAME, "");


            // Setup public trade topic
            publicTradeChannel = connection.createChannel();
            publicTradeChannel.exchangeDeclare(PUBLIC_TRADE_EXCHANGE_NAME, "fanout");

            // Setup snapshot topic
            snapshotChannel = connection.createChannel();
            snapshotChannel.exchangeDeclare(SNAPSHOT_EXCHANGE_NAME, "fanout");

        } catch (Exception e) {
            throw new RuntimeException("Cannot configure rabbit mq", e);
        }
    }

    public Channel getSubmitOrderChannel() {
        return orderChannel;
    }

    public Channel getPublicTradeChannel() {
        return publicTradeChannel;
    }

    public Channel getSnapshotChannel() {
        return snapshotChannel;
    }
}
