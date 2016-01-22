package net.sorted.exchange;


import java.io.IOException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import net.sorted.exchange.config.ExchangeConfig;
import net.sorted.exchange.config.RabbitMqConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SimulatedClient {
    public static final void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(ExchangeConfig.class);

        RabbitMqConfig rabbitMqConfig = ctx.getBean(RabbitMqConfig.class);
        Channel submit = rabbitMqConfig.getSubmitOrderChannel();
        String message = "{\"clientId\":\"dug\",\"instrument\":\"AMZN\",\"quantity\":1002,\"price\":\"100.12\",\"side\":\"SELL\",\"type\":\"LIMIT\"}";
        try {
            submit.basicPublish("", RabbitMqConfig.ORDER_SUBMIT_CHANNEL_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}