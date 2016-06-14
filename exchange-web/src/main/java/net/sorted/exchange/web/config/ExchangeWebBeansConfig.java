package net.sorted.exchange.web.config;

import net.sorted.exchange.web.OrderSnapshotCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class ExchangeWebBeansConfig {

    @Bean
    public OrderSnapshotCache orderSnapshotCache() {
        return new OrderSnapshotCache();
    }


    // Turn off CORS restrictions (ie allow any site to hit the REST endpoints)
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {

                registry.addMapping("/**").
                        allowedMethods("PUT", "GET", "POST", "DELETE", "OPTIONS").
                        allowCredentials(true).allowedOrigins("*").
                        allowedHeaders("Content-Type");
            }
        };
    }
}
