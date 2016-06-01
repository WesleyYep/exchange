package net.sorted.exchange.web.msghandler;


import java.io.IOException;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import net.sorted.exchange.messages.ExchangeMessage;
import net.sorted.exchange.web.ClientOrder;
import net.sorted.exchange.web.ClientOrderType;
import net.sorted.exchange.web.ClientPrivateTrade;
import net.sorted.exchange.web.ClientSide;
import net.sorted.exchange.web.ProtoToClientConverter;
import net.sorted.exchange.web.WebSocketSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class OrderUpdateListener {

    private final WebSocketSender webSocketSender;
    private final Channel orderUpdateChannel;
    private final Consumer consumer;

    private Logger log = LogManager.getLogger(OrderUpdateListener.class);

    @Autowired
    public OrderUpdateListener(WebSocketSender webSocketSender,
                               @Qualifier("orderUpdateChannel") Channel orderUpdateChannel,
                               @Qualifier("orderUpdateExchangeName") String orderUpdateExchangeName) throws IOException {
        this.webSocketSender = webSocketSender;
        this.orderUpdateChannel = orderUpdateChannel;

        String queueName = orderUpdateChannel.queueDeclare().getQueue();
        orderUpdateChannel.queueBind(queueName, orderUpdateExchangeName, "");
        log.debug("Bound queue {} to exchange {}", queueName, orderUpdateExchangeName);

        consumer = new DefaultConsumer(orderUpdateChannel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                ExchangeMessage.Order order = ExchangeMessage.Order.parseFrom(body);
                sendOrderUpdateToClient(order);
            }
        };

        orderUpdateChannel.basicConsume(queueName, true, consumer);
        log.debug("Started listening for private trades");
    }

    private void sendOrderUpdateToClient(ExchangeMessage.Order order) {
        log.debug("Received updated order '" + order + "'");
        log.debug("Sending updated order to {}", order.getSubmitter());

        // Spring's Json converter gets upset trying to convert Protobuf objects to Json
        // There are many fields that should not be converted so create a 'struct' from the protobuf object that exposes exactly what should be
        // sent back to the client
        ClientOrder o = ProtoToClientConverter.orderMessageToClientOrder(order);

        webSocketSender.sendMessageToUser("/queue/order.updates/" + order.getInstrument(), order.getSubmitter()+"", o);
    }

}
