package net.sorted.exchange.web.config;

import net.sorted.exchange.web.OrderSnapshotCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.sorted.exchange.web.WebSocketSender;

@Configuration
public class ExchangeWebBeansConfig {

    @Bean
    public OrderSnapshotCache orderSnapshotCache() {
        return new OrderSnapshotCache();
    }
}
