package net.sorted.exchange.web.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
