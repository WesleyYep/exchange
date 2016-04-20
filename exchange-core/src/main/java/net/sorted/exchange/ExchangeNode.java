package net.sorted.exchange;

import net.sorted.exchange.config.ExchangeConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ExchangeNode {

    public static void main(String[] args) {

        ApplicationContext ctx = new AnnotationConfigApplicationContext(ExchangeConfig.class);

        OrderMqReceivers receivers = ctx.getBean(OrderMqReceivers.class);
        receivers.startAll();

        System.out.println("Listening for orders");

    }
}
