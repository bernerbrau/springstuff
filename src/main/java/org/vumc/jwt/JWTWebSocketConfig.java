/* Project: continuum
 * File: JWTWebSocketConfig.java
 * Created: Apr 05, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import java.security.Principal;
import java.util.regex.Matcher;

import static org.vumc.jwt.JWTConstants.bearerPattern;

@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@Configuration
public class JWTWebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer
{
  private final JWTVerifier verifier;

  @Autowired
  public JWTWebSocketConfig(final JWTVerifier inVerifier)
  {
    verifier = inVerifier;
  }

  @Override
  public void registerStompEndpoints(final StompEndpointRegistry registry)
  {
    // nop
  }

  @Override
  public void configureClientInboundChannel(final ChannelRegistration registration)
  {
    registration.setInterceptors(new ChannelInterceptorAdapter()
    {

      @Override
      public Message<?> preSend(Message<?> message, MessageChannel channel)
      {
        StompHeaderAccessor accessor =
            MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand()))
        {
          Principal user = null;

          String headerValue = accessor.getFirstNativeHeader("Authorization");
          if (headerValue != null)
          {
            Matcher bearerMatcher = bearerPattern.matcher(headerValue);
            if (bearerMatcher.matches())
            {
              String token = bearerMatcher.group(1);
              user = verifier.verify(token);
            }
            else
            {
              throw new BadCredentialsException("No bearer token found in Authorization header");
            }
          }

          if (user != null)
          {
            accessor.setUser(user);
          }
          else
          {
            throw new AuthenticationCredentialsNotFoundException("No Authorization header found");
          }
        }

        return message;
      }
    });
  }

}
