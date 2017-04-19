/* Project: continuum
 * File: ContextRestoringWebSocketAdvice.java
 * Created: Apr 19, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.websockets;

import fj.function.Try0;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Map;

@Component
public class ContextRestoringWebSocketMessageAdvice
{
  private final UserDetailsService userManager;

  @Autowired
  public ContextRestoringWebSocketMessageAdvice(final UserDetailsService inUserManager)
  {
    userManager = inUserManager;
  }

  public <T,X extends Exception> T invoke(String wsUserId, Map<String,Object> wsSessionAttributes, Try0<T,X> action) throws X {
    RequestAttributes previousReqCtx = RequestContextHolder.getRequestAttributes();
    try
    {
      RequestAttributes request =
          (RequestAttributes) wsSessionAttributes.get(RequestStoringHandshakeInterceptor.ORIGINAL_REQUEST_ATTRIBUTE);
      RequestContextHolder.setRequestAttributes(request);
      SecurityContext previousSecCtx = SecurityContextHolder.getContext();
      try
      {
        UserDetails userDetails = userManager.loadUserByUsername(wsUserId);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(
            new PreAuthenticatedAuthenticationToken(userDetails, null, userDetails.getAuthorities()));

        return action.f();
      } finally {
        SecurityContextHolder.setContext(previousSecCtx);
      }
    } finally {
      RequestContextHolder.setRequestAttributes(previousReqCtx);
    }
  }
}
