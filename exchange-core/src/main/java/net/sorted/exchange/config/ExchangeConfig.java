package net.sorted.exchange.config;

import net.sorted.exchange.dao.OrderIdDao;
import net.sorted.exchange.dao.OrderIdDaoInMemory;
import net.sorted.exchange.OrderProcessorLocator;
import net.sorted.exchange.publishers.OrderSnapshotPublisher;
import net.sorted.exchange.publishers.OrderSnapshotPublisherRabbit;
import net.sorted.exchange.publishers.PrivateTradePublisher;
import net.sorted.exchange.publishers.PrivateTradePublisherRabbit;
import net.sorted.exchange.publishers.PublicTradePublisher;
import net.sorted.exchange.publishers.PublicTradePublisherRabbit;
import net.sorted.exchange.SubmitOrderReceiver;
import net.sorted.exchange.TradeIdDao;
import net.sorted.exchange.TradeIdDaoInMemory;
import net.sorted.exchange.messages.JsonConverter;
import net.sorted.exchange.orderbook.OrderBook;
import net.sorted.exchange.orderbook.OrderBookInMemory;
import net.sorted.exchange.orderprocessor.OrderProcessor;
import net.sorted.exchange.orderprocessor.OrderProcessorInMemory;
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

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public RabbitMqConfig rabbitMqConfig() {
        return new RabbitMqConfig(rabbitHostname);
    }

    @Bean
    public SubmitOrderReceiver submitOrderReceiver() {
        SubmitOrderReceiver receiver = new SubmitOrderReceiver(rabbitMqConfig().getSubmitOrderChannel(),
                RabbitMqConfig.ORDER_SUBMIT_QUEUE_NAME,
                orderProcessorLocator(),
                orderIdDao(),
                jsonConverter());

        return receiver;
    }

    @Bean
    public JsonConverter jsonConverter() {
        return new JsonConverter();
    }

    @Bean
    public OrderIdDao orderIdDao() {
        return new OrderIdDaoInMemory();
    }

    @Bean
    public TradeIdDao tradeIdDao() {
        return new TradeIdDaoInMemory();
    }

    @Bean
    public OrderProcessorLocator orderProcessorLocator() {
        OrderProcessorLocator locator =  new OrderProcessorLocator();

        String[] instruments = supportedInstrumentCSL.split(",");
        for (String instrument : instruments) {
            OrderBook amznOrderBook = new OrderBookInMemory(instrument, tradeIdDao());
            OrderProcessor orderProcessor = new OrderProcessorInMemory(amznOrderBook, privateTradePublisher(), publicTradePublisher(), orderSnapshotPublisher());
            locator.addOrderProcessor(instrument, orderProcessor);
        }

        return locator;
    }

    @Bean
    public PublicTradePublisher publicTradePublisher() {

        return new PublicTradePublisherRabbit(rabbitMqConfig().getPublicTradeChannel(), RabbitMqConfig.PUBLIC_TRADE_EXCHANGE_NAME, jsonConverter());
    }

    @Bean
    public PrivateTradePublisher privateTradePublisher() {
        return new PrivateTradePublisherRabbit();
    }

    @Bean
    public OrderSnapshotPublisher orderSnapshotPublisher() {
        return new OrderSnapshotPublisherRabbit(rabbitMqConfig().getSnapshotChannel(), RabbitMqConfig.SNAPSHOT_EXCHANGE_NAME, jsonConverter());
    }
}
