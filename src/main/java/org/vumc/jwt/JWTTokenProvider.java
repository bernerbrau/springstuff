/* Project: continuum
 * File: JWTTokenProvider.java
 * Created: Mar 27, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JWTTokenProvider
{
  private final byte[] jwtSecret;

  @Autowired
  public JWTTokenProvider(@Qualifier("jwtSecret") byte[] jwtSecret) {
    this.jwtSecret = jwtSecret;
  }

  public String newToken(Authentication auth) {
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
}
