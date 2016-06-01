package net.sorted.exchange.web.rest;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import net.sorted.exchange.messages.ExchangeMessage;
import net.sorted.exchange.web.ClientOrder;
import net.sorted.exchange.web.ClientOrderSearch;
import net.sorted.exchange.web.ClientOrderType;
import net.sorted.exchange.web.ClientSide;
import net.sorted.exchange.web.ProtoToClientConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderSearchService {
    private Logger log = LogManager.getLogger(OrderSearchService.class);

    private static final int MAX_ATTEMPTS_FOR_RESPONSE = 3;
    private static final long WAIT_FOR_RESPONSE_MS = 20000l;
    private final Channel orderSearchChannel;
    private final String orderSearchExchangeName;
    private final String replyQueueName;
    private final QueueingConsumer consumer;

    @Autowired
    public OrderSearchService(@Qualifier("orderSearchExchangeName") String orderSearchExchangeName,
                              @Qualifier("orderSearchChannel") Channel orderSearchChannel) throws IOException {
        this.orderSearchChannel = orderSearchChannel;
        this.orderSearchExchangeName = orderSearchExchangeName;

        // Setup a queue for responses to the requests made by this service
        replyQueueName = orderSearchChannel.queueDeclare().getQueue();
        consumer = new QueueingConsumer(orderSearchChannel);
        orderSearchChannel.basicConsume(replyQueueName, true, consumer);
    }

    @RequestMapping(value="/orders", method = {RequestMethod.GET })
    public List<ClientOrder> search(ClientOrderSearch searchParams, Principal principal) {
        log.info("Requesting orders matching {}", searchParams, principal.getName());


        if (searchParams.getSubmitter() == null) {
            searchParams.setSubmitter(principal.getName());
        }


        return doSearch(searchParams);
    }


    private List<ClientOrder> doSearch(ClientOrderSearch searchParams) {
        String correlationId = java.util.UUID.randomUUID().toString();

        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(correlationId)
                .replyTo(replyQueueName)
                .build();

        ExchangeMessage.OrderSearch.Builder request = ExchangeMessage.OrderSearch.newBuilder();
        if (searchParams.getSubmitter() != null) {
            request.setSubmitter(searchParams.getSubmitter());
        }
        if (searchParams.getInstrument() != null) {
            request.setInstrument(searchParams.getInstrument());
        }
        request.setFromTimestamp(searchParams.getFromTimestampMillis());
        request.setToTimestamp(searchParams.getToTimestampMillis());
        request.setOrderId(searchParams.getOrderId());
        request.setSearchType(ExchangeMessage.OrderSearch.Type.ALL_ORDERS); // TODO - either have different REST URL or add this field to ClientOrderSearch

        List<ClientOrder> results = null;

        try {
            // Send the request
            orderSearchChannel.basicPublish(orderSearchExchangeName, "", props, request.build().toByteArray());

            // Wait for the response
            for (int i=0; i<MAX_ATTEMPTS_FOR_RESPONSE; i++) {
                QueueingConsumer.Delivery delivery = consumer.nextDelivery(WAIT_FOR_RESPONSE_MS);
                if (delivery != null && delivery.getProperties().getCorrelationId().equals(correlationId)) {
                    ExchangeMessage.OrderSearchResults message = ExchangeMessage.OrderSearchResults.parseFrom(delivery.getBody());
                    results = searchResultsToClient(message);

                    break;
                }
            }
        } catch (Exception e) {
            // TODO handle this better
            log.error("Error getting orders for "+searchParams+" from the backend", e);
        }

        return results;
    }

    private List<ClientOrder> searchResultsToClient(ExchangeMessage.OrderSearchResults message) {

        List<ExchangeMessage.Order> orders = message.getOrdersList();
        return orders.stream().map(o -> ProtoToClientConverter.orderMessageToClientOrder(o)).collect(Collectors.toList());
    }


}
