package net.sorted.exchange.orders.msghandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.google.protobuf.Message;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import net.sorted.exchange.messages.ExchangeMessage;
import net.sorted.exchange.orders.domain.Order;
import net.sorted.exchange.orders.publishers.DomainWithMessageConverter;
import net.sorted.exchange.orders.repository.OrderRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OrderSearchHandler implements MessageReceiver {

    private final Channel channel;
    private final String queueName;
    private final Consumer consumer;
    private final OrderRepository orderRepository;


    private Logger log = LogManager.getLogger(OrderSearchHandler.class);


    public OrderSearchHandler(Channel channel, String queueName, OrderRepository orderRepository) {
        this.channel = channel;
        this.queueName = queueName;
        this.orderRepository = orderRepository;

        consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                handleOrderSearchMessage(consumerTag, envelope, properties, body);
            }
        };

        log.info("Ready to process orders searches");
    }

    @Override
    public void startReceiving() {

        try {
            channel.basicConsume(queueName, false, consumer);
            log.info("Started consuming order search messages from {} ", queueName);
        } catch (IOException e) {
            log.info("Error receiving order search messages. Listener stopping.", e);
            throw new RuntimeException("Error consuming message ", e);
        }

    }

    @Override
    public void stopReceiving() {
        try {
            channel.basicCancel(queueName);
            log.info("Stopped consuming messages from {} ", queueName);
        } catch (IOException e) {
            throw new RuntimeException("Error stopping listening for order messages", e);
        }
    }

    private void handleOrderSearchMessage(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

        String correlationId = properties.getCorrelationId();

        ExchangeMessage.OrderSearch request = ExchangeMessage.OrderSearch.parseFrom(body);

        try {
            List<Order> orders = processOrderSearch(request);

            AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(correlationId)
                    .build();

            ExchangeMessage.OrderSearchResults message = DomainWithMessageConverter.domainOrderListToProtobufMessage(orders);
            channel.basicPublish("", properties.getReplyTo(), replyProps, message.toByteArray());

            channel.basicAck(envelope.getDeliveryTag(), false);

            log.debug("Processed message '{}'", request);
        } catch (Throwable t) {
            log.info("Error processing message " + request, t);
            channel.basicNack(envelope.getDeliveryTag(), false, false);
        }
    }

    private List<Order> processOrderSearch(ExchangeMessage.OrderSearch request) {

        log.debug("Processing order search");

        List<Order> results = new ArrayList<>();

        // If the orderId is supplied, ignore any other criteria
        if (request.getOrderId() >= 0) {
            Order o = orderRepository.findOne(request.getOrderId());
            if (o != null) {
                results.add(o);
            }
        } else {
            long fromTimestamp = request.getFromTimestamp();
            if (fromTimestamp < 0) {
                fromTimestamp = 0;
            }
            long toTimestamp = request.getToTimestamp();
            if (toTimestamp < 0) {
                toTimestamp = Long.MAX_VALUE;
            }



            switch (request.getSearchType()) {
            case UNFILLED_ORDERS:
                results = unfilledSearch(request.getSubmitter(), request.getInstrument(), fromTimestamp, toTimestamp);
                break;
            case FILLED_ORDERS:
                // TODO - implement this
                break;
            case ALL_ORDERS:
                // TODO - implement this
                break;
            }

        }

        return results;
    }

    private List<Order> unfilledSearch(String submitter, String instrument, long from, long to) {


        if (instrument != null) {
            System.out.println("isEmpty: "+instrument.isEmpty());
        }

        if (submitter != null && submitter.isEmpty() == false) {
            if (instrument != null && instrument.isEmpty() == false) {
                return orderRepository.findUnfilledBySubmitterAndInstrumentId(submitter, instrument);
            } else {
                return orderRepository.findUnfilledBySubmitter(submitter);
            }
        } else {
            if (instrument != null && instrument.isEmpty() == false) {
                return orderRepository.findUnfilledByInstrumentId(instrument);
            }
        }

        return new ArrayList<>();
    }


}
