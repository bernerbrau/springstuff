/* Project: continuum
 * File: RequestStoringHandshakeInterceptor.java
 * Created: Apr 19, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.websockets;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class RequestStoringHandshakeInterceptor implements HandshakeInterceptor
{
  public static final String ORIGINAL_REQUEST_ATTRIBUTE = RequestStoringHandshakeInterceptor.class.getName() + ".original_request";

  @Override
  public boolean beforeHandshake(final ServerHttpRequest request,
                                 final ServerHttpResponse response,
                                 final WebSocketHandler wsHandler,
                                 final Map<String, Object> attributes)
      throws Exception
  {
    request.getAsyncRequestControl(response).start();
    attributes.put(ORIGINAL_REQUEST_ATTRIBUTE, new ServletWebRequest(((ServletServerHttpRequest)request).getServletRequest()));
    return true;
  }

  @Override
  public void afterHandshake(final ServerHttpRequest request, final ServerHttpResponse response,
                             final WebSocketHandler wsHandler, final Exception exception)
  {

  }
}
