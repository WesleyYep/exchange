package net.sorted.exchange.orders;


import java.io.IOException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import net.sorted.exchange.orders.config.ExchangeConfig;
import net.sorted.exchange.config.RabbitMqConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SimulatedClient {

    Channel submit;
    String clientId = "dug";

    public SimulatedClient(Channel submit, String clientId) {
        this.submit = submit;
        this.clientId =clientId;
    }

    public void submitLimitOrder(String instrument, int qty, String price, String side) {
        String message = "{\"clientId\":\"" + clientId + "\",\"instrument\":\"" + instrument + "\",\"quantity\":" + qty + ",\"price\":\"" + price + "\",\"side\":\"" + side + "\",\"type\":\"LIMIT\"}";
        try {
            submit.basicPublish(RabbitMqConfig.ORDER_SUBMIT_EXCHANGE_NAME, instrument, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static final void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(ExchangeConfig.class);

        RabbitMqConfig rabbitMqConfig = ctx.getBean(RabbitMqConfig.class);
        rabbitMqConfig.getSubmitOrderChannel("AMZN");
        SimulatedClient client = new SimulatedClient(rabbitMqConfig.getOrderChannel(), "doug");


        client.submitLimitOrder("AMZN", 500, "100.02", "SELL");
        client.submitLimitOrder("AMZN", 500, "100.03", "SELL");
        client.submitLimitOrder("AMZN", 500, "100.04", "SELL");

        client.submitLimitOrder("AMZN", 1000, "99.99", "BUY");
        client.submitLimitOrder("AMZN", 1000, "100.00", "BUY");
        client.submitLimitOrder("AMZN", 1000, "100.03", "BUY");


        client.submitLimitOrder("REJECT", 1000, "100.02", "SELL");


        System.exit(0);
    }
}