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
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.GeneralSecurityException;
import java.time.Instant;

@Component
class JWTVerifier extends JWTKeyUser
{
  Authentication verify(String jwtToken) throws JWTVerificationException
  {
    try
    {
      return JWTAuthenticationToken.fromTrustedJWT(
          JWT.require(Algorithm.HMAC512(jwtSecret.f()))
                 .acceptExpiresAt(Instant.now().getEpochSecond())
                 .build()
                 .verify(jwtToken));
    }
    catch (GeneralSecurityException inE)
    {
      throw new JWTVerificationException("JWT Secret exception", inE);
    }
  }
}
