package net.sorted.exchange;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import com.rabbitmq.client.Channel;
import net.sorted.exchange.messages.JsonConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class PublicTradePublisherRabbit implements PublicTradePublisher {

    private final Channel channel;
    private final String exchangeName;
    private final JsonConverter converter;

    private Logger log = LogManager.getLogger(PublicTradePublisherRabbit.class);

    public PublicTradePublisherRabbit(Channel channel, String exchangeName, JsonConverter converter) {
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
        String message = converter.publicTradeToJson(trade);
        try {
            byte[] bytes = message.getBytes("UTF-8");
            channel.basicPublish(exchangeName, "", null, bytes);
            log.debug("Published public trade " + message);
        } catch (UnsupportedEncodingException e) {
            // This should never be able to happen!!!
            log.error("Cannot convert trade message " + trade + " to json", e);
            throw new RuntimeException("Cannot convert trade message to json " + trade, e);
        } catch (IOException e) {
            log.error("Cannot publish public trade message", e);
            throw new RuntimeException("Error publishing public trade message to exchange " + exchangeName, e);
        }

    }
}
