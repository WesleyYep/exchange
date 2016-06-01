package net.sorted.exchange.orders.publishers;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import com.rabbitmq.client.Channel;
import net.sorted.exchange.messages.ExchangeMessage;
import net.sorted.exchange.orders.domain.Order;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.concurrent.ScheduledExecutorTask;


public class OrderUpdatePublisherRabbit implements OrderUpdatePublisher {

    private final Channel channel;
    private final String exchangeName;

    private Logger log = LogManager.getLogger(OrderUpdatePublisherRabbit.class);

    public OrderUpdatePublisherRabbit(Channel channel, String exchangeName) {
        this.channel = channel;
        this.exchangeName = exchangeName;
    }

    @Override
    public void publishUpdates(Set<Order> orders) {
        for (Order order : orders) {
            publishUpdate(order);
        }
    }

    @Override
    public void publishUpdate(Order order) {
        try {
            ExchangeMessage.Order message = DomainWithMessageConverter.domainOrderToProtobufMessage(order);
            channel.basicPublish(exchangeName, "", null, message.toByteArray());
            log.debug("Published order update " + message + " to exchange " + exchangeName);
        } catch (IOException e) {
            log.error("Cannot publish order update message", e);
            throw new RuntimeException("Error publishing order update message to exchange " + exchangeName, e);
        }
    }
}
