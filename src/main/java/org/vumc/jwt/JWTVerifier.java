/* Project: continuum
 * File: JWTVerifier.java
 * Created: Apr 05, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class JWTVerifier
{
  private final byte[] jwtSecret;

  @Autowired
  public JWTVerifier(@Qualifier("jwtSecret") byte[] jwtSecret) {
    this.jwtSecret = jwtSecret;
  }

  Authentication verify(String jwtToken) throws JWTVerificationException
  {
    return JWTAuthenticationToken.fromTrustedJWT(
        JWT.require(Algorithm.HMAC512(jwtSecret))
               .acceptExpiresAt(Instant.now().getEpochSecond())
               .build()
               .verify(jwtToken));
  }
}
