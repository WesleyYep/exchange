package net.sorted.exchange.web.config;

import java.io.IOException;
import com.rabbitmq.client.Channel;
import net.sorted.exchange.config.RabbitMqConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//@ConfigurationProperties(locations="classpath:exchange.properties", ignoreUnknownFields = false)
public class RabbitWebConfig {

    @Value("${rabbit.hostname}")
    private String rabbitHostname;

    @Bean
    public RabbitMqConfig rabbitMqConfig() {
        return new RabbitMqConfig(rabbitHostname);
    }

    @Bean
    @Qualifier("submitExchange")
    public Channel submitExchange() {
        return rabbitMqConfig().getOrderChannel();
    }

    @Bean
    @Qualifier("submitExchangeName")
    public String submitExchangeName() {
        return RabbitMqConfig.ORDER_SUBMIT_EXCHANGE_NAME;
    }


    @Bean
    public Channel privateTradeChannel() throws IOException {
        return rabbitMqConfig().getPrivateTradeChannel();
    }

    @Bean
    @Qualifier("privateTradeExchangeName")
    public String privateTradeExchangeName() {
        return RabbitMqConfig.PRIVATE_TRADE_EXCHANGE_NAME;
    }

    @Bean
    public Channel publicTradeChannel() throws IOException {
        return rabbitMqConfig().getPrivateTradeChannel();
    }

    @Bean
    @Qualifier("publicTradeExchangeName")
    public String publicTradeExchangeName() {
        return RabbitMqConfig.PUBLIC_TRADE_EXCHANGE_NAME;
    }


    @Bean
    @Qualifier("snapshotChannel")
    public Channel snapshotChannel() throws IOException {
        return rabbitMqConfig().getSnapshotPublishChannel();
    }

    @Bean
    @Qualifier("orderSnapshotRequestChannel")
    public Channel snapshotRequestChannel() {
        return rabbitMqConfig().getSnapshotRequestChannel();
    }

    @Bean
    @Qualifier("orderSnapshotRequestQueueName")
    public String orderSnapshotRequestQueueName() {
        return RabbitMqConfig.SNAPSHOT_REQUEST_QUEUE_NAME;
    }
}
