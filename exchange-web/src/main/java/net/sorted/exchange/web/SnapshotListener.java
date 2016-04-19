package net.sorted.exchange.web;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import net.sorted.exchange.config.RabbitMqConfig;
import net.sorted.exchange.messages.ExchangeMessage;
import net.sorted.exchange.messages.JsonConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class SnapshotListener {

    private final WebSocketSender webSocketSender;
    private final Channel snapshotChannel;
    private final Consumer consumer;

    private Logger log = LogManager.getLogger(SnapshotListener.class);

    @Autowired
    public SnapshotListener(WebSocketSender webSocketSender, @Qualifier("snapshotChannel") Channel snapshotChannel) throws IOException {
        this.webSocketSender = webSocketSender;
        this.snapshotChannel = snapshotChannel;

        String queueName = snapshotChannel.queueDeclare().getQueue();
        snapshotChannel.queueBind(queueName, RabbitMqConfig.SNAPSHOT_EXCHANGE_NAME, "");

        consumer = new DefaultConsumer(snapshotChannel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                ExchangeMessage.OrderBookSnapshot message = ExchangeMessage.OrderBookSnapshot.parseFrom(body);
                sendSnapshotToAll(message);
            }
        };

        snapshotChannel.basicConsume(queueName, true, consumer);
        log.debug("Started listening for snapshots");
    }

    private void sendSnapshotToAll(ExchangeMessage.OrderBookSnapshot message) {
        String instrumentId = message.getInstrumentId();

        ClientOrderSnapshot s = new ClientOrderSnapshot();
        s.setInstrumentId(instrumentId);
        List<ClientSnapshotLevel> buy = new ArrayList<>();
        for (ExchangeMessage.OrderBookLevel l : message.getBuysList()) {
            buy.add(new ClientSnapshotLevel(l.getPrice(), l.getQuantity()));
        }

        List<ClientSnapshotLevel> sell = new ArrayList<>();
        for (ExchangeMessage.OrderBookLevel l : message.getSellsList()) {
            sell.add(new ClientSnapshotLevel(l.getPrice(), l.getQuantity()));
        }

        s.setBuy(buy);
        s.setSell(sell);

        webSocketSender.sendMessage("/topic/snapshot/"+instrumentId, s);
        log.debug("Sent snapshot for instrument {} message= {}", instrumentId, s);
    }

}
