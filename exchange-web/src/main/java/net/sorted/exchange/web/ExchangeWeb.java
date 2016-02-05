package net.sorted.exchange.web;

import java.util.Arrays;

import net.sorted.exchange.SubmitOrderReceiver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("net.sorted.exchange")
@SpringBootApplication
public class ExchangeWeb extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ExchangeWeb.class);
    }

    public static void main(String[] args) {
        // SpringApplication.setAdditionalProfiles(…​)

        System.out.println("Getting spring context");
        ApplicationContext ctx = SpringApplication.run(ExchangeWeb.class, args);
        System.out.println("Got spring context");

        SubmitOrderReceiver submitOrderReceiver = ctx.getBean(SubmitOrderReceiver.class);
        submitOrderReceiver.startReceiving();
        System.out.println("Listening for orders");

//        System.out.println("Let's inspect the beans provided by Spring Boot:");
//
//        String[] beanNames = ctx.getBeanDefinitionNames();
//        Arrays.sort(beanNames);
//        for (String beanName : beanNames) {
//            System.out.println(beanName);
//        }

//        WebSocketSender sender = ctx.getBean(WebSocketSender.class);
//        Thread t = new Thread(new Sender(sender));
//        t.start();

    }

    private static class Sender implements Runnable {
        private final WebSocketSender websocket;

        public Sender(WebSocketSender websocket) {
            this.websocket = websocket;
        }


        @Override
        public void run() {
            int count = 0;
            while (true) {
                websocket.sendMessage("/topic/test", "" + count++);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }

}