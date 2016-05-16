package net.sorted.exchange.web.rest;

import java.io.IOException;
import java.security.Principal;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import net.sorted.exchange.messages.ExchangeMessage;
import net.sorted.exchange.web.ClientOrder;
import net.sorted.exchange.web.ClientSide;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderSubmission {

    @Autowired
    @Qualifier("submitExchange")
    private Channel channel;

    @Autowired
    @Qualifier("submitExchangeName")
    private String exchangeName;


    private Logger log = LogManager.getLogger(OrderSubmission.class);


    @RequestMapping(value="/orders", method = {RequestMethod.POST })
    public void newOrder(@RequestBody ClientOrder order, Principal principal) {
        log.info("Got a new order {} from {}", order, principal.getName());

        ExchangeMessage.Order.Builder exchangeOrder = ExchangeMessage.Order.newBuilder();

        String correlationId = order.getCorrelationId();
        if (correlationId == null) {
            exchangeOrder.clearCorrelationId();
        } else {
            exchangeOrder.setCorrelationId(correlationId);
        }

        exchangeOrder.setOrderId(order.getOrderId());
        exchangeOrder.setClientId(order.getClientId());
        exchangeOrder.setInstrument(order.getInstrument());
        exchangeOrder.setOrderType(ExchangeMessage.Order.OrderType.LIMIT);
        exchangeOrder.setPrice(order.getPrice());
        exchangeOrder.setQuantity(order.getQuantity());
        exchangeOrder.setSide((order.getSide() == ClientSide.BUY) ? ExchangeMessage.Side.BUY : ExchangeMessage.Side.SELL);
        exchangeOrder.setState(ExchangeMessage.Order.State.UNSUBMITTED);

        ExchangeMessage.Order o = exchangeOrder.build();
        try {
            channel.basicPublish(exchangeName, order.getInstrument(), MessageProperties.PERSISTENT_TEXT_PLAIN, o.toByteArray());
            log.info("Published order to exchange {} [order: {}]", exchangeName, o);
        } catch (IOException e) {
            log.error("Error receiving submit order message ", e);
            throw new RuntimeException("Error receiving submit order message ", e);
        }
    }
}
