/* Project: continuum
 * File: JWTKeyUser.java
 * Created: Apr 07, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.jwt;

import fj.function.Try0;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.security.GeneralSecurityException;

abstract class JWTKeyUser
{
  protected Try0<byte[], GeneralSecurityException> jwtSecret;

  @Autowired
  void setJwtSecret(@Qualifier("jwtSecret") Try0<byte[], GeneralSecurityException> jwtSecret) {
    this.jwtSecret = jwtSecret;
  }
}
