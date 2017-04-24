/* Project: continuum
 * File: DefinedAuthorities.java
 * Created: Apr 21, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum DefinedAuthority implements GrantedAuthority
{
  PROVIDER("provider",true),
  USER_ADMIN("useradmin",true),

  AUTHENTICATION_BROKER("authenticationbroker",false),
  PATIENT_SOURCE("patientsource",false),
  SYSTEM("system",false);

  public final String authority;
  public final String hasAuthority;
  public final boolean assignableByUserAdmin;

  DefinedAuthority(final String inAuthority,
                   final boolean assignableByUserAdmin)
  {
    this.authority = inAuthority;
    this.hasAuthority = "hasAuthority('" + inAuthority + "')";
    this.assignableByUserAdmin = assignableByUserAdmin;
  }

  public static List<DefinedAuthority> getAdminAssignableAuthorities() {
    return Stream.of(values())
        .filter(v -> v.assignableByUserAdmin)
        .collect(Collectors.toList());
  }

  @Override
  @JsonValue
  public String getAuthority()
  {
    return authority;
  }

  @JsonCreator
  public static DefinedAuthority fromString(String authority) {
    return Stream.of(values())
            .filter(a -> a.authority.equalsIgnoreCase(authority))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("Undefined authority: " + authority));
  }

  public static DefinedAuthority from(GrantedAuthority grantedAuthority) {
    return fromString(grantedAuthority.getAuthority());
  }

  public static List<DefinedAuthority> from(Collection<? extends GrantedAuthority> grantedAuthorities) {
    return grantedAuthorities.stream()
               .map(DefinedAuthority::from)
               .collect(Collectors.toList());
  }
}
