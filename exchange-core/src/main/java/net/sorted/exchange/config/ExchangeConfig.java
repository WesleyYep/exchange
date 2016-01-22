package net.sorted.exchange.config;

import net.sorted.exchange.OrderIdDao;
import net.sorted.exchange.OrderIdDaoInMemory;
import net.sorted.exchange.OrderProcessorLocator;
import net.sorted.exchange.OrderSnapshotPublisher;
import net.sorted.exchange.OrderSnapshotPublisherRabbit;
import net.sorted.exchange.PrivateTradePublisher;
import net.sorted.exchange.PrivateTradePublisherRabbit;
import net.sorted.exchange.PublicTradePublisher;
import net.sorted.exchange.PublicTradePublisherRabbit;
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
                RabbitMqConfig.ORDER_SUBMIT_CHANNEL_NAME,
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
        OrderBook amznOrderBook = new OrderBookInMemory(tradeIdDao());

        OrderProcessor orderProcessor = new OrderProcessorInMemory(amznOrderBook, privateTradePublisher(), publicTradePublisher(), orderSnapshotPublisher());
        locator.addOrderProcessor("AMZN", orderProcessor);

        return locator;
    }

    @Bean
    public PublicTradePublisher publicTradePublisher() {
        return new PublicTradePublisherRabbit();
    }

    @Bean
    public PrivateTradePublisher privateTradePublisher() {
        return new PrivateTradePublisherRabbit();
    }

    @Bean
    public OrderSnapshotPublisher orderSnapshotPublisher() {
        return new OrderSnapshotPublisherRabbit();
    }
}
