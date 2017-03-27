/* Project: continuum
 * File: JWTAuthenticationToken.java
 * Created: Mar 24, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.jwt;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.security.auth.Subject;
import java.security.Principal;
import java.util.Collection;
import java.util.stream.Collectors;

public class JWTAuthenticationToken extends AbstractAuthenticationToken
{
  private String principal;
  private String jwtToken;

  public JWTAuthenticationToken(final String jwtToken)
  {
    super(null);
    this.jwtToken = jwtToken;
    this.principal = null;
  }

  private JWTAuthenticationToken(final DecodedJWT jwtToken)
  {
    super(getAuthorities(jwtToken));
    this.jwtToken = null;
    this.principal = jwtToken.getSubject();
  }

  static Authentication fromTrustedJWT(final DecodedJWT trustedJWT) {
    JWTAuthenticationToken token = new JWTAuthenticationToken(trustedJWT);
    token.setAuthenticated(true);
    return token;
  }

  private static Collection<? extends GrantedAuthority> getAuthorities(final DecodedJWT jwtToken)
  {
    return jwtToken
               .getClaim("auth")
               .asList(String.class)
               .stream()
               .map(SimpleGrantedAuthority::new)
               .collect(Collectors.toList());
  }

  @Override
  public String getCredentials()
  {
    return jwtToken;
  }

  @Override
  public String getPrincipal()
  {
    return principal;
  }

  @Override
  public boolean implies(final Subject subject)
  {
    return subject.getPrincipals().stream()
        .map(Principal::getName)
        .filter(n -> n.equals(principal))
        .findAny()
        .isPresent();
  }

}
