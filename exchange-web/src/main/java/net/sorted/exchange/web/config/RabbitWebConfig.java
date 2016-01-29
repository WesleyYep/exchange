package net.sorted.exchange.web.config;

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
    @Qualifier("submitQueue")
    public Channel submitQueue() {
        return exchangeConfig.rabbitMqConfig().getSubmitOrderChannel();
    }

    @Bean
    @Qualifier("submitExchangeName")
    public String submitExchangeName() {

        return RabbitMqConfig.ORDER_SUBMIT_EXCHANGE_NAME;
    }
}
