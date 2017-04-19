/* Project: continuum
 * File: RunAsSystemAuthorityMethodInteceptor.java
 * Created: Apr 18, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.intercept.RunAsUserToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.vumc.model.Authority;

import java.util.Collections;

public class RunAsSystemAuthorityMethodInteceptor implements MethodInterceptor {
  @Override
  public Object invoke(final MethodInvocation invocation) throws Throwable
  {
    SecurityContext previousContext = SecurityContextHolder.getContext();
    try {
      SecurityContext systemContext = SecurityContextHolder.createEmptyContext();
      systemContext.setAuthentication(
          new RunAsUserToken("", "system", null, Collections.singleton(new Authority("system")), null));
      SecurityContextHolder.setContext(systemContext);
      return invocation.proceed();
    } finally {
      SecurityContextHolder.setContext(previousContext);
    }
  }
}
