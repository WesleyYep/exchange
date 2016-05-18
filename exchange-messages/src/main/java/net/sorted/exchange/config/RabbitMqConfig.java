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

    public static final String SNAPSHOT_REQUEST_QUEUE_NAME = "snapshot.request";


    public static final String PUBLIC_TRADE_EXCHANGE_NAME = "public.trade.exchange";
    public static final String PRIVATE_TRADE_EXCHANGE_NAME = "private.trade.exchange";

    public static final String SNAPSHOT_EXCHANGE_NAME = "snapshot.exchange";

    private Logger log = LogManager.getLogger(RabbitMqConfig.class);

    private final Map<String, Object> submitQueueArgs = new HashMap<String, Object>();

    private final Channel orderChannel;
    private final Channel publicTradeChannel;
    private final Channel privateTradeChannel;
    private final Channel snapshotPublishChannel;
    private final Channel snapshotRequestChannel;

    private final int connectionAttempts = 12;
    private final long connectionAttemptIntervalMillis = 5000;

    public RabbitMqConfig(String hostname) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(hostname);
        factory.setAutomaticRecoveryEnabled(true);
        Connection connection = null;
        try {
            log.debug("connecting to rabbit on host '{}' with auto recovery on", hostname);

            // Make a few attempts to connect in case RAbbit is not yet ready
            int attempt = 0;
            while (attempt < connectionAttempts) {
                try {
                    connection = factory.newConnection();
                    break;
                } catch (Exception c) {
                    log.info("Error connection to RabbitMq (attempt " + attempt + ")", c);
                    Thread.sleep(connectionAttemptIntervalMillis);
                    attempt++;
                }
            }

            if (connection == null) {
                throw new RuntimeException("Cannot connect to RabbitMq on host " + hostname + " after " + attempt + " attempts.");
            }

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
            snapshotPublishChannel = connection.createChannel();
            snapshotPublishChannel.exchangeDeclare(SNAPSHOT_EXCHANGE_NAME, "fanout");

            // Setup snapshot request queue
            snapshotRequestChannel = connection.createChannel();
            snapshotRequestChannel.queueDeclare(SNAPSHOT_REQUEST_QUEUE_NAME, false, false, false, null);
            snapshotRequestChannel.basicQos(1);


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

    public Channel getSnapshotPublishChannel() {
        return snapshotPublishChannel;
    }

    public Channel getSnapshotRequestChannel() { return snapshotRequestChannel; }
}
