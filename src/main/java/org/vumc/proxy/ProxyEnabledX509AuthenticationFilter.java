/* Project: continuum
 * File: ProxyEnabledX509AuthenticationFilter.java
 * Created: Apr 12, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.proxy;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.preauth.x509.X509AuthenticationFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProxyEnabledX509AuthenticationFilter extends X509AuthenticationFilter
{
  private final Pattern          subjectPrincipalRegex;
  private final String           subjectDNProxyHeader;
  private final GrantedAuthority brokerAuthority;

  private AuthenticationManager authenticationManager;

  public ProxyEnabledX509AuthenticationFilter(
      final String inSubjectDNProxyHeader,
      final String inSubjectPrincipalRegex,
      final GrantedAuthority inBrokerAuthority)
  {
    this.subjectDNProxyHeader = inSubjectDNProxyHeader;
    this.subjectPrincipalRegex = Pattern.compile(inSubjectPrincipalRegex);
    this.brokerAuthority = inBrokerAuthority;
  }

  @Override
  protected void successfulAuthentication(final HttpServletRequest request,
                                          final HttpServletResponse response,
                                          final Authentication authResult)
      throws IOException, ServletException
  {
    Authentication finalAuth = authResult;

    if (authResult.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(brokerAuthority.getAuthority()::equalsIgnoreCase)) {
      String proxySSLSubjectDN = request.getHeader(subjectDNProxyHeader);
      if (proxySSLSubjectDN != null) {
        Matcher matcher = subjectPrincipalRegex.matcher(proxySSLSubjectDN);
        if (matcher.find()) {
          String proxyPrincipal = matcher.group(1);
          finalAuth = authenticationManager
               .authenticate(new PreAuthenticatedAuthenticationToken(proxyPrincipal,
                                                                     finalAuth.getCredentials()));
          super.successfulAuthentication(request, response, finalAuth);
        }
      }
    }
  }

  public void setAuthenticationManager(final AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
    super.setAuthenticationManager(authenticationManager);
  }

}
