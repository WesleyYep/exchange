package net.sorted.exchange.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.broker.BrokerAvailabilityEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

// TODO - find out about ApplicationListener and BrokerAvailabilityEvent
@Component
public class WebSocketSender implements ApplicationListener<BrokerAvailabilityEvent> {

    private Logger log = LogManager.getLogger(WebSocketSender.class);

    private final SimpMessagingTemplate messagingTemplate;

    // TODO - find out where messagingTemplate comes from and how to do this without magic
    @Autowired
    public WebSocketSender(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        log.debug("WebSocket sender initialised");
    }

    public void sendMessage(String destination, Object content) {
        log.debug("Sending to websocket '" + destination + "' message: '"+ content + "");
        messagingTemplate.convertAndSend(destination, content);
    }

    @Override
    public void onApplicationEvent(BrokerAvailabilityEvent event) {
        log.debug("WebSocketSender got an application event " + event);
    }

    public void sendMessageToUser(String destination, String user, Object message) {
        log.debug("Send to destination {} and user {} message {}", destination, user, message);
        messagingTemplate.convertAndSendToUser(user, destination, message);
    }
}
