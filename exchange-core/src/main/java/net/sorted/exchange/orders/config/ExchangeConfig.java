package net.sorted.exchange.orders.config;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.rabbitmq.client.Channel;
import net.sorted.exchange.orders.MessageReceivers;
import net.sorted.exchange.orders.msghandler.OrderBookSnapshotRequestHandler;
import net.sorted.exchange.orders.msghandler.SubmitOrderReceiver;
import net.sorted.exchange.orders.dao.TradeIdDao;
import net.sorted.exchange.orders.dao.TradeIdDaoInMemory;
import net.sorted.exchange.config.RabbitMqConfig;
import net.sorted.exchange.orders.orderbook.OrderBook;
import net.sorted.exchange.orders.orderbook.OrderBookInMemory;
import net.sorted.exchange.orders.orderprocessor.OrderFillService;
import net.sorted.exchange.orders.orderprocessor.OrderProcessor;
import net.sorted.exchange.orders.orderprocessor.OrderProcessorDb;
import net.sorted.exchange.orders.publishers.OrderSnapshotPublisher;
import net.sorted.exchange.orders.publishers.OrderSnapshotPublisherRabbit;
import net.sorted.exchange.orders.publishers.OrderUpdatePublisher;
import net.sorted.exchange.orders.publishers.OrderUpdatePublisherRabbit;
import net.sorted.exchange.orders.publishers.PrivateTradePublisher;
import net.sorted.exchange.orders.publishers.PrivateTradePublisherRabbit;
import net.sorted.exchange.orders.publishers.PublicTradePublisher;
import net.sorted.exchange.orders.publishers.PublicTradePublisherRabbit;
import net.sorted.exchange.orders.repository.OrderFillRepository;
import net.sorted.exchange.orders.repository.OrderRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@PropertySource("classpath:exchange.properties")
public class ExchangeConfig {

    private Logger log = LogManager.getLogger(ExchangeConfig.class);

    @Value("${rabbit.hostname}")
    private String rabbitHostname;

    @Value("${instrumentCSL}")
    private String supportedInstrumentCSL;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderFillRepository orderFillRepository;

    @Autowired
    private OrderFillService orderFillService;

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
    public TradeIdDao tradeIdDao() {
        return tradeIdDao;
    }

    @Bean
    public Map<String, OrderBook> instrumentIdToOrderBook() {
        String[] instruments = supportedInstrumentCSL.split(",");
        Map<String, OrderBook> map = new HashMap<>();
        for (String instrument : instruments) {
            map.put(instrument, new OrderBookInMemory(instrument, tradeIdDao()));
        }

        log.debug("Created {} orderbooks ", instruments.length);

        return map;
    }

    @Bean
    public MessageReceivers messageReceivers() {
        MessageReceivers messageReceivers = new MessageReceivers();

        Map<String, OrderBook> instrumentIdToOrderBook = instrumentIdToOrderBook();

        String[] instruments = supportedInstrumentCSL.split(",");
        ExecutorService publisherExecutor = Executors.newWorkStealingPool(instruments.length);

        for (String instrument : instruments) {
            OrderBook orderBook = instrumentIdToOrderBook.get(instrument);

            OrderProcessor orderProcessor = new OrderProcessorDb(orderBook, orderRepository, orderFillRepository, privateTradePublisher(),
                    publicTradePublisher(), orderSnapshotPublisher(), orderUpdatePublisher(), publisherExecutor, orderFillService);

            String submitForInstrumentQueueName = rabbitMqConfig().getSubmitOrderQueue(instrument);
            Channel orderChannel = rabbitMqConfig().getOrderChannel();
            messageReceivers.addReceiver(new SubmitOrderReceiver(orderChannel, submitForInstrumentQueueName, orderProcessor));

            String snapshotForInstrumentQueueName = rabbitMqConfig().getOrderSnapshotRequestQueue(instrument);
            OrderBookSnapshotRequestHandler orderBookSnapshotRequestHandler =
                    new OrderBookSnapshotRequestHandler(rabbitMqConfig().getSnapshotRequestChannel(), snapshotForInstrumentQueueName, orderProcessor);

            messageReceivers.addReceiver(orderBookSnapshotRequestHandler);
        }

        return messageReceivers;
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
        return new OrderSnapshotPublisherRabbit(rabbitMqConfig().getSnapshotPublishChannel(), RabbitMqConfig.PUBLISH_SNAPSHOT_EXCHANGE_NAME);
    }

    @Bean
    public OrderUpdatePublisher orderUpdatePublisher() {
        return new OrderUpdatePublisherRabbit(rabbitMqConfig().getOrderUpdateChannel(), RabbitMqConfig.ORDER_UPDATE_EXCHANGE_NAME);
    }

}
