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
    @Qualifier("submitQueue")
    public Channel submitQueue() {
        return exchangeConfig.rabbitMqConfig().getSubmitOrderChannel();
    }

    @Bean
    @Qualifier("snapshotTopic")
    public Channel snapshotChannel() throws IOException {
        Channel channel = exchangeConfig.rabbitMqConfig().getSnapshotChannel();
//        String queueName = channel.queueDeclare().getQueue();
//        channel.queueBind(queueName, RabbitMqConfig.SNAPSHOT_EXCHANGE_NAME, "AMZN");

        return channel;
    }

    @Bean
    @Qualifier("submitExchangeName")
    public String submitExchangeName() {

        return RabbitMqConfig.ORDER_SUBMIT_EXCHANGE_NAME;
    }
}
