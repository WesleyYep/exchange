package net.sorted.exchange.web;


import java.io.IOException;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import net.sorted.exchange.domain.Trade;
import net.sorted.exchange.messages.JsonConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class PublicTradeListener {

    private final WebSocketSender webSocketSender;
    private final Channel publicTradeChannel;
    private final Consumer consumer;
    private final JsonConverter jsonConverter;

    private Logger log = LogManager.getLogger(PublicTradeListener.class);

    @Autowired
    public PublicTradeListener(WebSocketSender webSocketSender,
                               @Qualifier("publicTradeChannel") Channel publicTradeChannel,
                               @Qualifier("publicTradeExchangeName") String publicTradeExchangeName,
                               JsonConverter jsonConverter) throws IOException {
        this.webSocketSender = webSocketSender;
        this.publicTradeChannel = publicTradeChannel;
        this.jsonConverter = jsonConverter;

        String queueName = publicTradeChannel.queueDeclare().getQueue();
        publicTradeChannel.queueBind(queueName, publicTradeExchangeName, "");
        log.debug("Bound queue {} to exchange {}", queueName, publicTradeExchangeName);

        consumer = new DefaultConsumer(publicTradeChannel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                sendPublicTradeToClient(message);
            }
        };

        publicTradeChannel.basicConsume(queueName, true, consumer);
        log.debug("Started listening for public trades");
    }

    private void sendPublicTradeToClient(String message) {
        log.debug("Received public trade '" + message + "'");
        Trade t = jsonConverter.jsonToTrade(message);
        webSocketSender.sendMessage("/topic/public.trade/" + t.getInstrumentId(), message);
    }

}
