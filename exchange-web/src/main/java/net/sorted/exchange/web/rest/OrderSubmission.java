package net.sorted.exchange.web.rest;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import net.sorted.exchange.config.RabbitMqConfig;
import net.sorted.exchange.messages.ExchangeOrder;
import net.sorted.exchange.messages.JsonConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
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

    @Autowired
    private JsonConverter jsonConverter;


    private Logger log = LogManager.getLogger(OrderSubmission.class);


    @RequestMapping(value="/orders", method = {RequestMethod.POST })
    public void newOrder(@RequestBody ExchangeOrder order, Principal principal) {
        log.info("Got a new order {} from {}", order, principal.getName());

        order.setClientId(principal.getName());
        // TODO - should be using a better way to convert object to json (or not using json to the backend at all)
        String orderJson = jsonConverter.exchangeOrderToJson(order);

        try {
            channel.basicPublish(exchangeName, order.getInstrument(), MessageProperties.PERSISTENT_TEXT_PLAIN, orderJson.getBytes("UTF-8"));
        } catch (IOException e) {
            log.error("Error receiving submit order message ", e);
            throw new RuntimeException("Error receiving submit order message ", e);
        }
    }
}
