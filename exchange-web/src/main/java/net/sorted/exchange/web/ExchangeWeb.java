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

// If this is uncommented, no exchange nodes need to be started. This is useful for testing
//        SubmitOrderReceiver submitOrderReceiver = ctx.getBean(SubmitOrderReceiver.class);
//        submitOrderReceiver.startReceiving();
//        System.out.println("Listening for orders");

    }

}