/* Project: continuum
 * File: WebSocketConfig.java
 * Created: Mar 07, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.broker.AbstractBrokerMessageHandler;
import org.springframework.messaging.simp.broker.SimpleBrokerMessageHandler;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.vumc.hypermedia.messaging.PersistentEntityMessageConverter;
import org.vumc.websockets.ConvertingOutboundChannelInterceptor;
import org.vumc.websockets.RegisteringWebSocketHandlerDecoratorFactory;
import org.vumc.websockets.RequestStoringHandshakeInterceptor;
import org.vumc.websockets.SecureSubscriptionRegistry;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer
{
  private final PersistentEntityMessageConverter            hypermediaConverter;
  private final ConvertingOutboundChannelInterceptor        outboundInterceptor;
  private final RegisteringWebSocketHandlerDecoratorFactory decoratorFactory;

  @Autowired
  public WebSocketConfig(
      final PersistentEntityMessageConverter inHypermediaConverter,
      final ConvertingOutboundChannelInterceptor inOutboundInterceptor,
      final RegisteringWebSocketHandlerDecoratorFactory inDecoratorFactory)
  {
    hypermediaConverter = inHypermediaConverter;
    outboundInterceptor = inOutboundInterceptor;
    decoratorFactory = inDecoratorFactory;
  }

  @Override
  public boolean configureMessageConverters(final List<MessageConverter> messageConverters)
  {
    // messageConverters.add(hypermediaConverter);
    return super.configureMessageConverters(messageConverters);
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config)
  {
    config.enableSimpleBroker("/topic");
    config.setApplicationDestinationPrefixes("/app");
  }

  @Override
  public void configureClientOutboundChannel(final ChannelRegistration registration)
  {
    registration.setInterceptors(outboundInterceptor);
  }

  @Override
  public void configureWebSocketTransport(final WebSocketTransportRegistration registration)
  {
    registration.addDecoratorFactory(decoratorFactory);
    super.configureWebSocketTransport(registration);
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry)
  {
    registry.addEndpoint("/stomp")
        .setAllowedOrigins("*")
        .withSockJS()
          .setInterceptors(new RequestStoringHandshakeInterceptor())
          .setSessionCookieNeeded(false);
  }

  @Autowired
  public void setSubscriptionRegistry(@Qualifier("simpleBrokerMessageHandler") AbstractBrokerMessageHandler messageHandler,
                                      SecureSubscriptionRegistry registry) {
    ((SimpleBrokerMessageHandler)messageHandler).setSubscriptionRegistry(registry);
  }

}