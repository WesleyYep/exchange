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

        ApplicationContext ctx = SpringApplication.run(ExchangeWeb.class, args);

        SubmitOrderReceiver submitOrderReceiver = ctx.getBean(SubmitOrderReceiver.class);
        submitOrderReceiver.startReceiving();
        System.out.println("Listening for orders");

        System.out.println("Let's inspect the beans provided by Spring Boot:");

        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            System.out.println(beanName);
        }
    }

}