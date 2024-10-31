package com.example.tic_tac_toe.ws;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	 @Override
	  public void configureMessageBroker(MessageBrokerRegistry config) {
	    config.setApplicationDestinationPrefixes("/app");
	    config.setUserDestinationPrefix("/user");
	  }

	  @Override
	  public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/websocket");
	    //registry.addEndpoint("/websocket").addInterceptors();
	  }

}