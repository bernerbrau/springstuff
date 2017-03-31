/* Project: continuum
 * File: JWTSecurityContextRepository.java
 * Created: Mar 28, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class JWTSecurityContextRepository implements SecurityContextRepository
{
  private static final Pattern bearerPattern = Pattern.compile("^Bearer\\s+(.+)$");

  private final byte[] jwtSecret;

  @Autowired
  public JWTSecurityContextRepository(@Qualifier("jwtSecret") byte[] jwtSecret) {
    this.jwtSecret = jwtSecret;
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
          JWTAuthenticationToken.fromTrustedJWT(
              verify(
                  jwtToken)));
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
      response.setHeader("X-Auth-Token", newToken(authentication));
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

  private String newToken(Authentication auth) {
    if (!auth.isAuthenticated())
    {
      throw new JWTCreationException("Could not create JWT Token",
            new IllegalArgumentException("Unauthorized username and password"));
    }

    ZonedDateTime now = ZonedDateTime.now();
    Instant expiry = now.plus(10, ChronoUnit.YEARS).toInstant();

    Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();

    String[] authorityNames = authorities.stream()
                                  .map(GrantedAuthority::getAuthority)
                                  .collect(Collectors.toList())
                                  .toArray(new String[0]);

    return JWT.create()
               .withJWTId(UUID.randomUUID().toString())
               .withIssuedAt(Date.from(now.toInstant()))
               .withExpiresAt(Date.from(expiry))
               .withSubject(auth.getName())
               .withArrayClaim("auth", authorityNames)
               .sign(Algorithm.HMAC512(jwtSecret));
  }

  private DecodedJWT verify(String jwtToken) throws JWTVerificationException
  {
    return JWT.require(Algorithm.HMAC512(jwtSecret))
               .acceptExpiresAt(Instant.now().getEpochSecond())
               .build()
               .verify(jwtToken);
  }
}
