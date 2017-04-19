/* Project: continuum
 * File: RegisteringWebSocketSessionDecorator.java
 * Created: Apr 19, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.websockets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;

@Component
public class RegisteringWebSocketHandlerDecoratorFactory implements WebSocketHandlerDecoratorFactory
{
  private final WebSocketSessionRegistry registry;

  @Autowired
  public RegisteringWebSocketHandlerDecoratorFactory(final WebSocketSessionRegistry inRegistry)
  {
    registry = inRegistry;
  }

  @Override
  public WebSocketHandler decorate(final WebSocketHandler handler)
  {
    return new WebSocketHandlerDecorator(handler) {
      @Override
      public void afterConnectionEstablished(final WebSocketSession session) throws Exception
      {
        registry.register(session);
        super.afterConnectionEstablished(session);
      }

      @Override
      public void afterConnectionClosed(final WebSocketSession session,
                                        final CloseStatus closeStatus)
          throws Exception
      {
        registry.unregister(session);
        super.afterConnectionClosed(session, closeStatus);
      }
    };
  }
}
