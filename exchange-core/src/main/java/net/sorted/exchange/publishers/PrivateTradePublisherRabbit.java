package net.sorted.exchange.publishers;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import com.rabbitmq.client.Channel;
import net.sorted.exchange.domain.Trade;
import net.sorted.exchange.messages.JsonConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PrivateTradePublisherRabbit implements PrivateTradePublisher {

    private final Channel channel;
    private final String exchangeName;
    private final JsonConverter converter;

    private Logger log = LogManager.getLogger(PrivateTradePublisherRabbit.class);

    public PrivateTradePublisherRabbit(Channel channel, String exchangeName, JsonConverter converter) {
        this.channel = channel;
        this.exchangeName = exchangeName;
        this.converter = converter;
    }

    @Override
    public void publishTrades(List<Trade> trades) {
        for (Trade trade : trades) {
            publishTrade(trade);
        }
    }

    @Override
    public void publishTrade(Trade trade) {
        String message = converter.privateTradeToJson(trade);
        try {
            byte[] bytes = message.getBytes("UTF-8");
            channel.basicPublish(exchangeName, "", null, bytes);
            log.debug("Published private trade " + message);
        } catch (UnsupportedEncodingException e) {
            // This should never be able to happen!!!
            log.error("Cannot convert trade message " + trade + " to json", e);
            throw new RuntimeException("Cannot convert trade message to json " + trade, e);
        } catch (IOException e) {
            log.error("Cannot publish private trade message", e);
            throw new RuntimeException("Error publishing private trade message to exchange " + exchangeName, e);
        }
    }
}
