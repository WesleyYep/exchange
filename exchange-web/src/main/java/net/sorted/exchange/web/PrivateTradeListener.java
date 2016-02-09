package net.sorted.exchange.web;


import java.io.IOException;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import net.sorted.exchange.config.RabbitMqConfig;
import net.sorted.exchange.domain.Trade;
import net.sorted.exchange.messages.JsonConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class PrivateTradeListener {

    private final WebSocketSender webSocketSender;
    private final Channel privateTradeChannel;
    private final Consumer consumer;
    private final JsonConverter jsonConverter;

    private Logger log = LogManager.getLogger(PrivateTradeListener.class);

    @Autowired
    public PrivateTradeListener(WebSocketSender webSocketSender,
                                @Qualifier("privateTradeChannel") Channel privateTradeChannel,
                                @Qualifier("privateTradeExchangeName") String privateTradeExchangeName,
                                JsonConverter jsonConverter) throws IOException {
        this.webSocketSender = webSocketSender;
        this.privateTradeChannel = privateTradeChannel;
        this.jsonConverter = jsonConverter;

        String queueName = privateTradeChannel.queueDeclare().getQueue();
        privateTradeChannel.queueBind(queueName, privateTradeExchangeName, "");
        log.debug("Bound queue {} to exchange {}", queueName, privateTradeExchangeName);

        consumer = new DefaultConsumer(privateTradeChannel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                sendPrivateTradeToClient(message);
            }
        };

        privateTradeChannel.basicConsume(queueName, true, consumer);
        log.debug("Started listening for private trades");
    }

    private void sendPrivateTradeToClient(String message) {
        log.debug("Received private trade '" + message + "'");

        // need to extract the clientId from the trade
        Trade t = jsonConverter.jsonToTrade(message);

        log.debug("Sending private trade to {}", t.getClientId());
        webSocketSender.sendMessageToUser("/queue/private.trade/" + t.getInstrumentId(), t.getClientId(), message);
    }

}
