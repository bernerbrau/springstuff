/* Project: continuum
 * File: ProxyPreAuthenticationFilter.java
 * Created: Apr 12, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.proxy;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import javax.servlet.http.HttpServletRequest;

public class ProxyPreAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter
{

  public ProxyPreAuthenticationFilter(AuthenticationManager authenticationManager) {
    setAuthenticationManager(authenticationManager);
    setCheckForPrincipalChanges(true);
    setInvalidateSessionOnPrincipalChange(false);
  }

  @Override
  protected Object getPreAuthenticatedPrincipal(final HttpServletRequest request)
  {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (userIsUserVerifier(authentication)) {
      return request.getHeader("X-Identified-User");
    }
    return null;
  }

  @Override
  protected Object getPreAuthenticatedCredentials(final HttpServletRequest request)
  {
    return new Object();
  }

  @Override
  protected boolean principalChanged(final HttpServletRequest request,
                                     final Authentication currentAuthentication)
  {
    return userIsUserVerifier(currentAuthentication) ||
           super.principalChanged(request, currentAuthentication);
  }


  private boolean userIsUserVerifier(final Authentication inAuthentication)
  {
    return inAuthentication.isAuthenticated() &&
           inAuthentication.getAuthorities().stream()
               .map(GrantedAuthority::getAuthority)
               .filter(a -> a.equalsIgnoreCase("userverifier"))
               .findAny().isPresent();
  }

}
