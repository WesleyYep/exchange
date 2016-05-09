package net.sorted.exchange.orders.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.rabbitmq.client.Channel;
import net.sorted.exchange.orders.OrderMqReceivers;
import net.sorted.exchange.orders.SubmitOrderReceiver;
import net.sorted.exchange.orders.dao.TradeIdDao;
import net.sorted.exchange.orders.dao.TradeIdDaoInMemory;
import net.sorted.exchange.config.RabbitMqConfig;
import net.sorted.exchange.orders.dao.OrderDao;
import net.sorted.exchange.orders.dao.OrderDaoInMemory;
import net.sorted.exchange.orders.orderbook.OrderBook;
import net.sorted.exchange.orders.orderbook.OrderBookInMemory;
import net.sorted.exchange.orders.orderprocessor.OrderProcessor;
import net.sorted.exchange.orders.orderprocessor.OrderProcessorDb;
import net.sorted.exchange.orders.orderprocessor.OrderProcessorInMemory;
import net.sorted.exchange.orders.publishers.OrderSnapshotPublisher;
import net.sorted.exchange.orders.publishers.OrderSnapshotPublisherRabbit;
import net.sorted.exchange.orders.publishers.PrivateTradePublisher;
import net.sorted.exchange.orders.publishers.PrivateTradePublisherRabbit;
import net.sorted.exchange.orders.publishers.PublicTradePublisher;
import net.sorted.exchange.orders.publishers.PublicTradePublisherRabbit;
import net.sorted.exchange.orders.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private OrderRepository orderRepository;

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
//            OrderProcessor orderProcessor = new OrderProcessorInMemory(orderBook, orderIdDao(), privateTradePublisher(), publicTradePublisher(), orderSnapshotPublisher(), publisherExecutor);
            OrderProcessor orderProcessor = new OrderProcessorDb(orderBook, orderRepository, privateTradePublisher(), publicTradePublisher(), orderSnapshotPublisher(), publisherExecutor);
            String instrumentQueueName = rabbitMqConfig().getSubmitOrderChannel(instrument);
            Channel orderChannel = rabbitMqConfig().getOrderChannel();
            SubmitOrderReceiver receiver = new SubmitOrderReceiver(orderChannel,
                    instrumentQueueName,
                    orderProcessor);

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
