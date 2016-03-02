package net.sorted.exchange.web.config;

import java.io.IOException;
import com.rabbitmq.client.Channel;
import net.sorted.exchange.config.ExchangeConfig;
import net.sorted.exchange.config.RabbitMqConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//@ConfigurationProperties(locations="classpath:exchange.properties", ignoreUnknownFields = false)
public class RabbitWebConfig {

    @Autowired
    private ExchangeConfig exchangeConfig;

    @Bean
    @Qualifier("submitExchange")
    public Channel submitExchange() {
        return exchangeConfig.rabbitMqConfig().getOrderChannel();
    }

    @Bean
    @Qualifier("submitExchangeName")
    public String submitExchangeName() {
        return RabbitMqConfig.ORDER_SUBMIT_EXCHANGE_NAME;
    }


    @Bean
    public Channel privateTradeChannel() throws IOException {
        return exchangeConfig.rabbitMqConfig().getPrivateTradeChannel();
    }

    @Bean
    @Qualifier("privateTradeExchangeName")
    public String privateTradeExchangeName() {
        return RabbitMqConfig.PRIVATE_TRADE_EXCHANGE_NAME;
    }

    @Bean
    public Channel publicTradeChannel() throws IOException {
        return exchangeConfig.rabbitMqConfig().getPrivateTradeChannel();
    }

    @Bean
    @Qualifier("publicTradeExchangeName")
    public String publicTradeExchangeName() {
        return RabbitMqConfig.PUBLIC_TRADE_EXCHANGE_NAME;
    }


    @Bean
    @Qualifier("snapshotChannel")
    public Channel snapshotChannel() throws IOException {
        return exchangeConfig.rabbitMqConfig().getSnapshotChannel();
    }


}
