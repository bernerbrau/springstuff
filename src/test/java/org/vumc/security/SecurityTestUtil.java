/* Project: continuum
 * File: SecurityTestUtil.java
 * Created: May 04, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.security;

import fj.function.Try0;
import fj.function.TryEffect0;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.vumc.model.DefinedAuthority;

import java.util.Collections;

public enum SecurityTestUtil
{
  ;

  public static <T,X extends Exception>
    T withAuthority(final DefinedAuthority authority, final Try0<T,X> callable) throws X
  {
    SecurityContext previousContext = SecurityContextHolder.getContext();
    try {
      SecurityContextImpl withAuthority = new SecurityContextImpl();
      withAuthority.setAuthentication(
          new PreAuthenticatedAuthenticationToken("testuser","",
            Collections.singletonList(authority)));
      SecurityContextHolder.setContext(withAuthority);
      return callable.f();
    } finally {
      SecurityContextHolder.setContext(previousContext);
    }
  }

  public static <X extends Exception>
    void withAuthority(final DefinedAuthority authority, final TryEffect0<X> callable) throws X
  {
    SecurityContext previousContext = SecurityContextHolder.getContext();
    try {
      SecurityContextImpl withAuthority = new SecurityContextImpl();
      withAuthority.setAuthentication(
          new PreAuthenticatedAuthenticationToken("testuser","",
            Collections.singletonList(authority)));
      SecurityContextHolder.setContext(withAuthority);
      callable.f();
    } finally {
      SecurityContextHolder.setContext(previousContext);
    }
  }
}
