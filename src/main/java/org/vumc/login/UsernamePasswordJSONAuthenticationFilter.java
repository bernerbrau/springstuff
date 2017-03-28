/* Project: continuum
 * File: UsernamePasswordJSONAuthenticationFilter.java
 * Created: Mar 28, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.login;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Component
public class UsernamePasswordJSONAuthenticationFilter extends UsernamePasswordAuthenticationFilter
{
  private static final String JSON_BODY_ATTRIBUTE_NAME =
      UsernamePasswordJSONAuthenticationFilter.class.getCanonicalName() + ".JSONBody";
  private final ObjectMapper        objectMapper;

  @Autowired
  public UsernamePasswordJSONAuthenticationFilter(final ObjectMapper inObjectMapper,
                                                  @Lazy final AuthenticationManager authManager)
  {
    objectMapper = inObjectMapper;
    setAuthenticationManager(authManager);
  }

  @Override
  public Authentication attemptAuthentication(final HttpServletRequest request,
                                              final HttpServletResponse response)
      throws AuthenticationException
  {
    try
    {
      request.setAttribute(
          JSON_BODY_ATTRIBUTE_NAME,
          this.objectMapper.readValue(request.getReader(),
              new TypeReference<Map<String, String>>(){}));
    }
    catch (IOException inE)
    {
      throw new InternalAuthenticationServiceException("Could not read request", inE);
    }

    return super.attemptAuthentication(request, response);
  }

  @Override
  protected String obtainPassword(final HttpServletRequest request)
  {
    return getJsonBody(request).get(getPasswordParameter());
  }

  @Override
  protected String obtainUsername(final HttpServletRequest request)
  {
    return getJsonBody(request).get(getUsernameParameter());
  }

  // We write this value in attemptAuthentication, so we know the cast is safe.
  @SuppressWarnings("unchecked")
  private Map<String, String> getJsonBody(final HttpServletRequest inRequest)
  {
    return (Map<String, String>) inRequest.getAttribute(JSON_BODY_ATTRIBUTE_NAME);
  }

  @Override
  protected void successfulAuthentication(final HttpServletRequest request,
                                          final HttpServletResponse response,
                                          final FilterChain chain, final Authentication authResult)
      throws IOException, ServletException
  {
    SecurityContextHolder.getContext().setAuthentication(authResult);
    response.setStatus(HttpStatus.OK.value());
  }

}
