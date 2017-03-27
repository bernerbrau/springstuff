/* Project: continuum
 * File: JWTAuthenticationProvider.java
 * Created: Mar 24, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class JWTAuthenticationProvider implements AuthenticationProvider
{
  private final byte[] jwtSecret;

  @Autowired
  public JWTAuthenticationProvider(@Qualifier("jwtSecret") byte[] jwtSecret) {
    this.jwtSecret = jwtSecret;
  }

  @Override
  public Authentication authenticate(final Authentication authentication)
      throws AuthenticationException
  {
    JWTAuthenticationToken jwt = (JWTAuthenticationToken) authentication;

    try
    {
      DecodedJWT trusted = JWT.require(Algorithm.HMAC512(jwtSecret))
                              .acceptExpiresAt(Instant.now().getEpochSecond())
                              .build()
                              .verify(jwt.getCredentials());
      return JWTAuthenticationToken.fromTrustedJWT(trusted);
    }
    catch (JWTVerificationException e) {
      throw new BadCredentialsException("JWT Verify Failed", e);
    }
  }

  @Override
  public boolean supports(final Class<?> authentication)
  {
    return authentication == JWTAuthenticationToken.class;
  }
}
