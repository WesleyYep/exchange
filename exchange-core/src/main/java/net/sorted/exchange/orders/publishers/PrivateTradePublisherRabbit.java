package net.sorted.exchange.orders.publishers;


import java.io.IOException;
import java.util.List;
import com.rabbitmq.client.Channel;
import net.sorted.exchange.orders.domain.Side;
import net.sorted.exchange.orders.domain.Trade;
import net.sorted.exchange.messages.ExchangeMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

public class PrivateTradePublisherRabbit implements PrivateTradePublisher {

    private final Channel channel;
    private final String exchangeName;

    private Logger log = LogManager.getLogger(PrivateTradePublisherRabbit.class);

    public PrivateTradePublisherRabbit(Channel channel, String exchangeName) {
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
        ExchangeMessage.PrivateTrade message = domainTradeToProtobufMessage(trade);
        try {
            channel.basicPublish(exchangeName, "", null, message.toByteArray());
            log.debug("Published private trade " + message);
        } catch (IOException e) {
            log.error("Cannot publish private trade message", e);
            throw new RuntimeException("Error publishing private trade message to exchange " + exchangeName, e);
        }
    }

    private ExchangeMessage.PrivateTrade domainTradeToProtobufMessage(Trade trade) {

        ExchangeMessage.PrivateTrade.Builder msg = ExchangeMessage.PrivateTrade.newBuilder();

        msg.setTradeId(trade.getTradeId());
        msg.setInstrumentId(trade.getInstrumentId());
        msg.setQuantity(trade.getQuantity());
        msg.setPrice(trade.getPrice());
        msg.setSide((trade.getSide() == Side.BUY) ? ExchangeMessage.Side.BUY : ExchangeMessage.Side.SELL);

        String clientId = trade.getClientId();
        if (clientId != null) {
            msg.setClientId(clientId);
        } else {
            msg.clearClientId();
        }

        DateTime tradeDate = trade.getTradeDate();
        if (tradeDate != null) {
            msg.setTradeDateMillisSinceEpoch(tradeDate.getMillis());
        } else {
            msg.clearTradeDateMillisSinceEpoch();
        }

        Long orderId = trade.getOrderId();
        if (orderId != null) {
            msg.setOrderId(orderId);
        } else {
            msg.clearOrderId();
        }

        return msg.build();
    }
}
