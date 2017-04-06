/* Project: continuum
 * File: JWTSecurityContextRepository.java
 * Created: Mar 28, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Matcher;

import static org.vumc.jwt.JWTConstants.bearerPattern;

@Component
public class JWTSecurityContextRepository implements SecurityContextRepository
{
  private final JWTSigner signer;
  private final JWTVerifier verifier;

  @Autowired
  public JWTSecurityContextRepository(final JWTSigner inSigner, final JWTVerifier inVerifier)
  {
    signer = inSigner;
    verifier = inVerifier;
  }

  @Override
  public SecurityContext loadContext(final HttpRequestResponseHolder requestResponseHolder)
  {
    SecurityContext context = SecurityContextHolder.getContext();
    if (context == null) {
      context = SecurityContextHolder.createEmptyContext();
    }
    String jwtToken = obtainToken(requestResponseHolder.getRequest());
    if (jwtToken != null) {
      context.setAuthentication(
          verifier.verify(
                  jwtToken));
      requestResponseHolder.getResponse().setHeader("X-Auth-Token", jwtToken);
    }
    return context;
  }

  @Override
  public void saveContext(final SecurityContext context, final HttpServletRequest request,
                          final HttpServletResponse response)
  {
    Authentication authentication = context.getAuthentication();
    if (authentication != null
        && authentication.isAuthenticated()
        && !(authentication instanceof AnonymousAuthenticationToken)
        && !(authentication instanceof JWTAuthenticationToken))
    {
      response.setHeader("X-Auth-Token", signer.sign(authentication));
    }
  }

  @Override
  public boolean containsContext(final HttpServletRequest request)
  {
    return request.getHeader("Authorization") != null
           && bearerPattern.matcher(request.getHeader("Authorization")).matches();
  }

  private String obtainToken(final HttpServletRequest inRequest)
  {
    String authHeader = inRequest.getHeader("Authorization");
    if (authHeader == null) {
      return null;
    }
    Matcher authMatcher = bearerPattern.matcher(authHeader);
    if(authMatcher.matches()) {
      return authMatcher.group(1);
    } else {
      return null;
    }
  }


}
