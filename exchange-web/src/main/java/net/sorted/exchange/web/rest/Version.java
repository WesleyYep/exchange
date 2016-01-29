package net.sorted.exchange.web.rest;

import com.rabbitmq.client.Channel;
import net.sorted.exchange.messages.ExchangeOrder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Version {


    private Logger log = LogManager.getLogger(Version.class);

    @RequestMapping(value="/version", method = {RequestMethod.GET })
    public int version() {
        log.info("Got a version request");
        System.out.println("Got a version request");
        return 99;
    }
}
