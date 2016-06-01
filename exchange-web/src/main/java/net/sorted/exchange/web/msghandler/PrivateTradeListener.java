package net.sorted.exchange.web.msghandler;


import java.io.IOException;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import net.sorted.exchange.messages.ExchangeMessage;
import net.sorted.exchange.web.ClientPrivateTrade;
import net.sorted.exchange.web.WebSocketSender;
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

    private Logger log = LogManager.getLogger(PrivateTradeListener.class);

    @Autowired
    public PrivateTradeListener(WebSocketSender webSocketSender,
                                @Qualifier("privateTradeChannel") Channel privateTradeChannel,
                                @Qualifier("privateTradeExchangeName") String privateTradeExchangeName) throws IOException {
        this.webSocketSender = webSocketSender;
        this.privateTradeChannel = privateTradeChannel;

        String queueName = privateTradeChannel.queueDeclare().getQueue();
        privateTradeChannel.queueBind(queueName, privateTradeExchangeName, "");
        log.debug("Bound queue {} to exchange {}", queueName, privateTradeExchangeName);

        consumer = new DefaultConsumer(privateTradeChannel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                ExchangeMessage.PrivateTrade trade = ExchangeMessage.PrivateTrade.parseFrom(body);
                sendPrivateTradeToClient(trade);
            }
        };

        privateTradeChannel.basicConsume(queueName, true, consumer);
        log.debug("Started listening for private trades");
    }

    private void sendPrivateTradeToClient(ExchangeMessage.PrivateTrade trade) {
        log.debug("Received private trade '" + trade + "'");
        log.debug("Sending private trade to {}", trade.getClientId());

        // Springs Json converter gets upset trying to convert Protobuf objects to Json
        // There are many fields that should not be converted so create a 'struct' from the protobuf object that exposes exactly what should be
        // sent back to the client
        ClientPrivateTrade t = new ClientPrivateTrade();
        t.setTradeId(trade.getTradeId());
        t.setInstrumentId(trade.getInstrumentId());
        t.setQuantity(trade.getQuantity());
        t.setPrice(trade.getPrice());
        t.setSide( (trade.getSide() == ExchangeMessage.Side.BUY) ? ClientPrivateTrade.Side.BUY : ClientPrivateTrade.Side.SELL );
        t.setTradeDateMillisSinceEpoch(trade.getTradeDateMillisSinceEpoch());
        t.setClientId(trade.getClientId());
        t.setOrderId(trade.getOrderId());
        t.setOrderSubmitter(trade.getOrderSubmitter());

        webSocketSender.sendMessageToUser("/queue/private.trade/" + trade.getInstrumentId(), trade.getOrderSubmitter()+"", t);
    }

}
