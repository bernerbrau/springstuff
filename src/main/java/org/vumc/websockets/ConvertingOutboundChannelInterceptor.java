/* Project: continuum
 * File: ContextRestoringWebSocketAdvice.java
 * Created: Apr 19, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.websockets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.user.SimpSession;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.ExecutorChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Map;

@Component
public class ConvertingOutboundChannelInterceptor extends ChannelInterceptorAdapter implements ExecutorChannelInterceptor
{
  private final MessageConverter                       brokerMessageConverter;
  private final ContextRestoringWebSocketMessageAdvice messageAdvice;
  private final SimpUserRegistry                       simpUserRegistry;
  private final WebSocketSessionRegistry               webSocketSessionRegistry;

  @Autowired
  public ConvertingOutboundChannelInterceptor(
      @Qualifier("brokerMessageConverter")
      @Lazy final MessageConverter inBrokerMessageConverter,
      final ContextRestoringWebSocketMessageAdvice inMessageAdvice,
      @Lazy final SimpUserRegistry inSimpUserRegistry,
      final WebSocketSessionRegistry inWebSocketSessionRegistry)
  {
    brokerMessageConverter = inBrokerMessageConverter;
    messageAdvice = inMessageAdvice;
    simpUserRegistry = inSimpUserRegistry;
    webSocketSessionRegistry = inWebSocketSessionRegistry;
  }

  @Override
  public Message<?> beforeHandle(final Message<?> message, final MessageChannel channel,
                                 final MessageHandler handler)
  {
    Object payload = message.getPayload();
    if (!(payload instanceof byte[] || payload instanceof String))
    {
      SimpMessageHeaderAccessor accessor =
          MessageHeaderAccessor.getAccessor(message, SimpMessageHeaderAccessor.class);
      if (accessor.getMessageType() == SimpMessageType.MESSAGE)
      {
        return messageAdvice.invoke(
            getUserName(accessor),
            getSessionAttributes(accessor),
            () -> brokerMessageConverter.toMessage(payload, message.getHeaders()));
      }
    }
    return message;
  }

  private String getUserName(final SimpMessageHeaderAccessor inAccessor)
  {
    Principal user = inAccessor.getUser();
    if (user != null) {
      return user.getName();
    }
    String sessionId = inAccessor.getSessionId();
    return simpUserRegistry.getUsers()
               .stream()
               .filter(u -> u.getSessions().stream().map(SimpSession::getId).anyMatch(sessionId::equalsIgnoreCase))
               .findAny()
               .map(SimpUser::getName)
               .orElse(null);
  }

  private Map<String,Object> getSessionAttributes(final SimpMessageHeaderAccessor inAccessor)
  {
    Map<String,Object> sessionAttributes = inAccessor.getSessionAttributes();
    if (sessionAttributes != null) {
      return sessionAttributes;
    }
    String sessionId = inAccessor.getSessionId();
    return webSocketSessionRegistry.lookup(sessionId).getAttributes();
  }

  @Override
  public void afterMessageHandled(final Message<?> message, final MessageChannel channel,
                                  final MessageHandler handler, final Exception ex)
  {
  }

}
