package net.sorted.exchange.web.rest;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AvailableInstruments {

    private final String[] all = { "AMZN", "GOOG", "WBC", "CBA" };
    private final String[] tech = { "AMZN", "GOOG" };

    private final Map<String, String[]> userToInstruments = new HashMap<>();

    private Logger log = LogManager.getLogger(AvailableInstruments.class);

    // TODO - write a proper service not just hard coded.
    public AvailableInstruments() {
        userToInstruments.put("doug", all);
        userToInstruments.put("john", tech);
    }

    @RequestMapping(value="/instruments", method = {RequestMethod.GET })
    public String[] newOrder(Principal principal) {
        log.info("Requesting instruments available for {}", principal.getName());

        return userToInstruments.get(principal.getName());
    }
}
