package net.sorted.exchange;

import net.sorted.exchange.config.ExchangeConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Exchange {

    public static final void main(String[] args) {
        ApplicationContext ctx =
                new AnnotationConfigApplicationContext(ExchangeConfig.class);

        SubmitOrderReceiver submitOrderReceiver = ctx.getBean(SubmitOrderReceiver.class);
        submitOrderReceiver.startReceiving();
        System.out.println("Listening for orders");

        //JobLauncher launcher=(JobLauncher)context.getBean("launcher");
    }
}
