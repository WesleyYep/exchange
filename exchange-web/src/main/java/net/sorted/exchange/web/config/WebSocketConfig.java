package net.sorted.exchange.web.config;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.handler.invocation.HandlerMethodReturnValueHandler;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.server.HandshakeInterceptor;



// TODO - get to understand this class
// TODO -- what is @EnableWebSocketMessageBroker
// TODO -- what is WebSocketMessageBrokerConfigurer
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    private Logger log = LogManager.getLogger(WebSocketConfig.class);

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/queue", "/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        HandshakeInterceptor interceptor = new HandshakeInterceptor() {
            @Override
            public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
                //log.debug("Websocket handshake for user {} to URI ", request.getPrincipal(), request.getURI());

                return true;
            }

            @Override
            public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

            }
        };
        registry.addEndpoint("/exchange").withSockJS().setInterceptors(interceptor);
    }


    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {

        //
        // This should allow tracking of user connections but only seems to get DISCONNECTs
        //

        ChannelInterceptor interceptor = new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
//                MessageHeaders headers = message.getHeaders();
//                SimpMessageType type = (SimpMessageType) headers.get("simpMessageType");
//                String simpSessionId = (String) headers.get("simpSessionId");
//
//                if (type == SimpMessageType.CONNECT) {
//                    Principal principal = (Principal) headers.get("simpUser");
//                    log.debug("WsSession {}  is connected for user {} ", simpSessionId, principal.getName());
//                } else if (type == SimpMessageType.DISCONNECT) {
//                    log.debug("WsSession {} is disconnected", simpSessionId);
//                }
                return message;
            }

            @Override
            public void postSend(Message<?> message, MessageChannel channel, boolean sent) {

            }

            @Override
            public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {

            }

            @Override
            public boolean preReceive(MessageChannel channel) {
                return true;
            }

            @Override
            public Message<?> postReceive(Message<?> message, MessageChannel channel) {
                return message;
            }

            @Override
            public void afterReceiveCompletion(Message<?> message, MessageChannel channel, Exception ex) {

            }
        };
        registration.setInterceptors(interceptor);
    }

//    @Override
//    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
//
//    }
//
//
//    @Override
//    public void configureClientOutboundChannel(ChannelRegistration registration) {
//
//    }
//
//    @Override
//    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
//
//    }
//
//    @Override
//    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
//
//    }
//
//    @Override
//    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
//        return false;
//    }


}
