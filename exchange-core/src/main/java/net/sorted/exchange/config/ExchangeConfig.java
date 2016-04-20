package net.sorted.exchange.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.rabbitmq.client.Channel;
import net.sorted.exchange.OrderMqReceivers;
import net.sorted.exchange.SubmitOrderReceiver;
import net.sorted.exchange.TradeIdDao;
import net.sorted.exchange.TradeIdDaoInMemory;
import net.sorted.exchange.dao.OrderDao;
import net.sorted.exchange.dao.OrderDaoInMemory;
import net.sorted.exchange.orderbook.OrderBook;
import net.sorted.exchange.orderbook.OrderBookInMemory;
import net.sorted.exchange.orderprocessor.OrderProcessor;
import net.sorted.exchange.orderprocessor.OrderProcessorInMemory;
import net.sorted.exchange.publishers.OrderSnapshotPublisher;
import net.sorted.exchange.publishers.OrderSnapshotPublisherRabbit;
import net.sorted.exchange.publishers.PrivateTradePublisher;
import net.sorted.exchange.publishers.PrivateTradePublisherRabbit;
import net.sorted.exchange.publishers.PublicTradePublisher;
import net.sorted.exchange.publishers.PublicTradePublisherRabbit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@PropertySource("classpath:exchange.properties")
public class ExchangeConfig {

    @Value("${rabbit.hostname}")
    private String rabbitHostname;

    @Value("${instrumentCSL}")
    private String supportedInstrumentCSL;

    private final OrderDao orderDao = new OrderDaoInMemory();
    private final TradeIdDao tradeIdDao = new TradeIdDaoInMemory();

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public RabbitMqConfig rabbitMqConfig() {
        return new RabbitMqConfig(rabbitHostname);
    }



    @Bean
    public OrderDao orderIdDao() {
        return orderDao;
    }

    @Bean
    public TradeIdDao tradeIdDao() {
        return tradeIdDao;
    }

    @Bean
    public OrderMqReceivers orderMqReceivers() {
        OrderMqReceivers orderMqReceivers = new OrderMqReceivers();

        String[] instruments = supportedInstrumentCSL.split(",");
        ExecutorService publisherExecutor = Executors.newWorkStealingPool(instruments.length);

        for (String instrument : instruments) {
            OrderBook orderBook = new OrderBookInMemory(instrument, tradeIdDao());
            OrderProcessor orderProcessor = new OrderProcessorInMemory(orderBook, privateTradePublisher(), publicTradePublisher(), orderSnapshotPublisher(), publisherExecutor);
            String instrumentQueueName = rabbitMqConfig().getSubmitOrderChannel(instrument);
            Channel orderChannel = rabbitMqConfig().getOrderChannel();
            SubmitOrderReceiver receiver = new SubmitOrderReceiver(orderChannel,
                    instrumentQueueName,
                    orderProcessor,
                    orderIdDao());

            orderMqReceivers.addReceiver(receiver);
        }

        return orderMqReceivers;
    }

    @Bean
    public PublicTradePublisher publicTradePublisher() {
        return new PublicTradePublisherRabbit(rabbitMqConfig().getPublicTradeChannel(), RabbitMqConfig.PUBLIC_TRADE_EXCHANGE_NAME);
    }

    @Bean
    public PrivateTradePublisher privateTradePublisher() {
        return new PrivateTradePublisherRabbit(rabbitMqConfig().getPrivateTradeChannel(), RabbitMqConfig.PRIVATE_TRADE_EXCHANGE_NAME);
    }

    @Bean
    public OrderSnapshotPublisher orderSnapshotPublisher() {
        return new OrderSnapshotPublisherRabbit(rabbitMqConfig().getSnapshotChannel(), RabbitMqConfig.SNAPSHOT_EXCHANGE_NAME);
    }
}
