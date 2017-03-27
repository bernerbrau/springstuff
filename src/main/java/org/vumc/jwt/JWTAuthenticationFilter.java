/* Project: continuum
 * File: JWTFilter.java
 * Created: Mar 27, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class JWTAuthenticationFilter extends AbstractAuthenticationProcessingFilter
{
  private static final Pattern bearerPattern = Pattern.compile("^Bearer\\s+(.+)$");

  @Autowired
  public JWTAuthenticationFilter(@Lazy AuthenticationManager manager) {
    super(new AntPathRequestMatcher("/**"));
    setAuthenticationManager(manager);
  }

  @Override
  public Authentication attemptAuthentication(final HttpServletRequest request,
                                              final HttpServletResponse response)
      throws AuthenticationException, IOException, ServletException
  {
    String jwtToken = obtainToken(request);
    if (jwtToken == null) {
      throw new AuthenticationCredentialsNotFoundException("No JWT Token found");
    }
    return getAuthenticationManager().authenticate(new JWTAuthenticationToken(jwtToken));
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request,
                                          HttpServletResponse response,
                                          FilterChain chain,
                                          Authentication authResult) throws IOException, ServletException {
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(authResult);
    SecurityContextHolder.setContext(context);
    chain.doFilter(request, response);
  }

  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            AuthenticationException failed) throws IOException, ServletException {
    SecurityContextHolder.clearContext();
    response.setStatus(401);
    response.flushBuffer();
  }

  private String obtainToken(final HttpServletRequest inRequest)
  {
    String authHeader = inRequest.getHeader("Authorization");
    if (authHeader == null) {
      return null;
    }
    Matcher authMatcher = bearerPattern.matcher(authHeader);
    if (authMatcher.matches()) {
      return authMatcher.group(1);
    } else {
      return null;
    }
  }

}
