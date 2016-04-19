package net.sorted.exchange.publishers;

import java.io.IOException;
import java.util.List;
import com.rabbitmq.client.Channel;
import net.sorted.exchange.domain.Trade;
import net.sorted.exchange.messages.ExchangeMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;


public class PublicTradePublisherRabbit implements PublicTradePublisher {

    private final Channel channel;
    private final String exchangeName;

    private Logger log = LogManager.getLogger(PublicTradePublisherRabbit.class);

    public PublicTradePublisherRabbit(Channel channel, String exchangeName) {
        this.channel = channel;
        this.exchangeName = exchangeName;
    }

    @Override
    public void publishTrades(List<Trade> trades) {
        for (Trade trade : trades) {
            publishTrade(trade);
        }
    }

    @Override
    public void publishTrade(Trade trade) {
        ExchangeMessage.PublicTrade message = domainTradeToProtobufMessage(trade);
        try {
            channel.basicPublish(exchangeName, "", null, message.toByteArray());
            log.debug("Published public trade " + message);
        } catch (IOException e) {
            log.error("Cannot publish public trade message", e);
            throw new RuntimeException("Error publishing public trade message to exchange " + exchangeName, e);
        }
    }

    private ExchangeMessage.PublicTrade domainTradeToProtobufMessage(Trade trade) {
        ExchangeMessage.PublicTrade.Builder msg = ExchangeMessage.PublicTrade.newBuilder();

        msg.setInstrumentId(trade.getInstrumentId());
        msg.setQuantity(trade.getQuantity());
        msg.setPrice(trade.getPrice());

        DateTime tradeDate = trade.getTradeDate();
        if (tradeDate != null) {
            msg.setTradeDateMillisSinceEpoch(tradeDate.getMillis());
        } else {
            msg.clearTradeDateMillisSinceEpoch();
        }

        return msg.build();
    }
}
