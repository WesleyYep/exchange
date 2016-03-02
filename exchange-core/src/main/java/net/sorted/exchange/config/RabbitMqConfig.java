package net.sorted.exchange.config;


import java.util.HashMap;
import java.util.Map;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RabbitMqConfig {
    public static final String ORDER_SUBMIT_EXCHANGE_NAME = "submit.order.exchange";
    public static final String ORDER_SUBMIT_QUEUE_NAME = "submit.order";
    public static final String ORDER_SUBMIT_DEAD_CHANNEL_NAME = "submit.order.dead";
    public static final String ORDER_SUBMIT_DEAD_EXCHANGE = "submit.order.dead.exchange";

    public static final String PUBLIC_TRADE_EXCHANGE_NAME = "public.trade.exchange";
    public static final String PRIVATE_TRADE_EXCHANGE_NAME = "private.trade.exchange";

    public static final String SNAPSHOT_EXCHANGE_NAME = "snapshot.exchange";

    private Logger log = LogManager.getLogger(RabbitMqConfig.class);

    private final Map<String, Object> submitQueueArgs = new HashMap<String, Object>();

    private final Channel orderChannel;
    private final Channel publicTradeChannel;
    private final Channel privateTradeChannel;
    private final Channel snapshotChannel;

    public RabbitMqConfig(String hostname) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(hostname);
        Connection connection = null;
        try {
            connection = factory.newConnection();
            orderChannel = connection.createChannel();

            // Setup Submit Order exchange with dead message exchange
            String deadQueueName = orderChannel.queueDeclare(ORDER_SUBMIT_DEAD_CHANNEL_NAME, false, false, false, null).getQueue();
            orderChannel.exchangeDeclare(ORDER_SUBMIT_DEAD_EXCHANGE, "direct");
            orderChannel.queueBind(deadQueueName, ORDER_SUBMIT_DEAD_EXCHANGE, "");
            orderChannel.exchangeDeclare(ORDER_SUBMIT_EXCHANGE_NAME, "direct");
            submitQueueArgs.put("x-dead-letter-exchange", ORDER_SUBMIT_DEAD_EXCHANGE);

            // Setup public trade topic
            publicTradeChannel = connection.createChannel();
            publicTradeChannel.exchangeDeclare(PUBLIC_TRADE_EXCHANGE_NAME, "fanout");

            // Setup private trade topic
            privateTradeChannel = connection.createChannel();
            privateTradeChannel.exchangeDeclare(PRIVATE_TRADE_EXCHANGE_NAME, "fanout");

            // Setup snapshot topic
            snapshotChannel = connection.createChannel();
            snapshotChannel.exchangeDeclare(SNAPSHOT_EXCHANGE_NAME, "fanout");

        } catch (Exception e) {
            throw new RuntimeException("Cannot configure rabbit mq", e);
        }
    }

    // Add a queue to the ORDER_SUBMIT exchange for a specific instrument
    // Name of bound queue is returned
    public String getSubmitOrderChannel(String instrument) {
        String instrumentQueueName = ORDER_SUBMIT_QUEUE_NAME + "-" + instrument;
        try {
            // NB - queue per instrument bound to channel
            orderChannel.queueDeclare(instrumentQueueName, false, false, false, submitQueueArgs);
            orderChannel.queueBind(instrumentQueueName, ORDER_SUBMIT_EXCHANGE_NAME, instrument);
            orderChannel.basicQos(1);
            log.info("bound queue {} to exchange {}", instrumentQueueName, ORDER_SUBMIT_EXCHANGE_NAME);
        } catch (Exception e) {
            throw new RuntimeException("Cannot configure rabbit mq with queue for submitting instrument " + instrument, e);
        }

        return instrumentQueueName;
    }

    public Channel getOrderChannel() {
        return orderChannel;
    }

    public Channel getPublicTradeChannel() {
        return publicTradeChannel;
    }

    public Channel getPrivateTradeChannel() {
        return privateTradeChannel;
    }

    public Channel getSnapshotChannel() {
        return snapshotChannel;
    }
}
