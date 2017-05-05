/* Project: continuum
 * File: InternalSystemAuthentication.java
 * Created: May 05, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.security;

import org.springframework.core.task.TaskDecorator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.vumc.model.DefinedAuthority;

import java.util.Collection;
import java.util.Collections;

public class InternalSystemAuthentication implements Authentication
{

  public TaskDecorator getTaskDecorator() {
    return r -> () -> {
      SecurityContext context = SecurityContextHolder.createEmptyContext();
      context.setAuthentication(this);
      SecurityContextHolder.setContext(context);
      try
      {
        r.run();
      }
      finally
      {
        SecurityContextHolder.clearContext();
      }
    };
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities()
  {
    return Collections.singletonList(DefinedAuthority.SYSTEM);
  }

  @Override
  public Object getCredentials()
  {
    return new Object();
  }

  @Override
  public Object getDetails()
  {
    return new Object();
  }

  @Override
  public Object getPrincipal()
  {
    return "system";
  }

  @Override
  public boolean isAuthenticated()
  {
    return true;
  }

  @Override
  public void setAuthenticated(final boolean isAuthenticated) throws IllegalArgumentException
  {
    if (!isAuthenticated) {
      throw new IllegalArgumentException("Cannot set isAuthenticated false for system auth");
    }
  }

  @Override
  public String getName()
  {
    return "system";
  }
}
