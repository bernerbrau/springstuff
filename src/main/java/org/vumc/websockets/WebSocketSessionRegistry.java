/* Project: continuum
 * File: WebSocketSessionRegistry.java
 * Created: Apr 19, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.websockets;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketSessionRegistry
{
  private Map<String, WebSocketSession> webSocketSessionLookup = new ConcurrentHashMap<>();

  public void register(WebSocketSession session) {
    webSocketSessionLookup.put(session.getId(), session);
  }

  public void unregister(WebSocketSession session) {
    webSocketSessionLookup.remove(session.getId());
  }

  public WebSocketSession lookup(String sessionId) {
    return webSocketSessionLookup.get(sessionId);
  }

}
