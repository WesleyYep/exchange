package net.sorted.exchange.web.msghandler;


import java.io.IOException;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import net.sorted.exchange.messages.ExchangeMessage;
import net.sorted.exchange.web.ClientPublicTrade;
import net.sorted.exchange.web.WebSocketSender;
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

    private Logger log = LogManager.getLogger(PublicTradeListener.class);

    @Autowired
    public PublicTradeListener(WebSocketSender webSocketSender,
                               @Qualifier("publicTradeChannel") Channel publicTradeChannel,
                               @Qualifier("publicTradeExchangeName") String publicTradeExchangeName) throws IOException {
        this.webSocketSender = webSocketSender;
        this.publicTradeChannel = publicTradeChannel;

        String queueName = publicTradeChannel.queueDeclare().getQueue();
        publicTradeChannel.queueBind(queueName, publicTradeExchangeName, "");
        log.debug("Bound queue {} to exchange {}", queueName, publicTradeExchangeName);

        consumer = new DefaultConsumer(publicTradeChannel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                ExchangeMessage.PublicTrade trade = ExchangeMessage.PublicTrade.parseFrom(body);
                sendPublicTradeToClient(trade);
            }
        };

        publicTradeChannel.basicConsume(queueName, true, consumer);
        log.debug("Started listening for public trades");
    }

    private void sendPublicTradeToClient(ExchangeMessage.PublicTrade trade) {
        log.debug("Received public trade '" + trade + "'");

        // Springs Json converter gets upset trying to convert Protobuf objects to Json
        // There are many fields that should not be converted so create a 'struct' from the protobuf object that exposes exactly what should be
        // sent back to the client
        ClientPublicTrade t = new ClientPublicTrade();
        t.setInstrumentId(trade.getInstrumentId());
        t.setQuantity(trade.getQuantity());
        t.setPrice(trade.getPrice());
        t.setTradeDateMillisSinceEpoch(trade.getTradeDateMillisSinceEpoch());

        webSocketSender.sendMessage("/topic/public.trade/" + trade.getInstrumentId(), t);
    }
}
