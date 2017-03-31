/* Project: continuum
 * File: WebSocketConfig.java
 * Created: Mar 07, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.vumc.hypermedia.messaging.PersistentEntityMessageConverter;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer
{
  private final PersistentEntityMessageConverter hypermediaConverter;

  @Autowired
  public WebSocketConfig(final PersistentEntityMessageConverter inHypermediaConverter)
  {
    hypermediaConverter = inHypermediaConverter;
  }

  @Override
  public boolean configureMessageConverters(final List<MessageConverter> messageConverters)
  {
    messageConverters.add(hypermediaConverter);
    return super.configureMessageConverters(messageConverters);
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config)
  {
    config.enableSimpleBroker("/topic");
    config.setApplicationDestinationPrefixes("/app");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry)
  {
    registry.addEndpoint("/stomp")
        .setAllowedOrigins("*")
        .withSockJS();
  }

}