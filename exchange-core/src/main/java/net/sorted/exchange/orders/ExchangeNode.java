package net.sorted.exchange.orders;

import java.util.Arrays;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@ComponentScan("net.sorted.exchange.orders")
@EnableJpaRepositories(basePackages = {"net.sorted.exchange.orders.repository"})
@EnableTransactionManagement
@SpringBootApplication
public class ExchangeNode  {


    public static void main(String[] args) {

        ApplicationContext ctx = SpringApplication.run(ExchangeNode.class, args);
        MessageReceivers receivers = ctx.getBean(MessageReceivers.class);
        receivers.startAll();


        System.out.println("Let's inspect the beans provided by Spring Boot:");

        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            System.out.println(beanName);
        }
    }

}


//public class ExchangeNode {
//
//    public static void main(String[] args) {
//
//        ApplicationContext ctx = new AnnotationConfigApplicationContext(ExchangeConfig.class);
//
//        OrderMqReceivers receivers = ctx.getBean(OrderMqReceivers.class);
//        receivers.startAll();
//
//        System.out.println("Listening for orders");
//
//    }
//}
