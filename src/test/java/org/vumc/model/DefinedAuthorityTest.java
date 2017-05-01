package org.vumc.model;

import org.junit.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class DefinedAuthorityTest
{
  @Test(expected=IllegalArgumentException.class)
  public void invalidAuthorityStringThrowsException()
  {
    try
    {
      DefinedAuthority.fromString("bogus");
    }
    catch (IllegalArgumentException e)
    {
      assertThat("Undefined authority: bogus", equalTo(e.getMessage()));
      throw e;
    }
  }

  @Test
  public void hasProperAuthorityCode()
  {
    assertThat("provider", equalTo(DefinedAuthority.PROVIDER.getAuthority()));
    assertThat("useradmin", equalTo(DefinedAuthority.USER_ADMIN.getAuthority()));
    assertThat("authenticationbroker", equalTo(DefinedAuthority.AUTHENTICATION_BROKER.getAuthority()));
    assertThat("patientsource", equalTo(DefinedAuthority.PATIENT_SOURCE.getAuthority()));
    assertThat("system", equalTo(DefinedAuthority.SYSTEM.getAuthority()));
  }

  @Test
  public void validAuthorityStringFindsEnum()
  {
    assertThat(DefinedAuthority.PROVIDER, equalTo(DefinedAuthority.fromString("provider")));
    assertThat(DefinedAuthority.USER_ADMIN, equalTo(DefinedAuthority.fromString("useradmin")));
    assertThat(DefinedAuthority.AUTHENTICATION_BROKER, equalTo(DefinedAuthority.fromString("authenticationbroker")));
    assertThat(DefinedAuthority.PATIENT_SOURCE, equalTo(DefinedAuthority.fromString("patientsource")));
    assertThat(DefinedAuthority.SYSTEM, equalTo(DefinedAuthority.fromString("system")));
  }

  @Test
  public void fromStringIsCaseInsensitive()
  {
    assertThat(DefinedAuthority.PROVIDER, equalTo(DefinedAuthority.fromString("pRoViDeR")));
  }

  @Test
  public void matchingGrantedAuthorityFindsEnum()
  {
    assertThat(DefinedAuthority.PROVIDER, equalTo(DefinedAuthority.from(new SimpleGrantedAuthority("provider"))));
    assertThat(DefinedAuthority.USER_ADMIN, equalTo(DefinedAuthority.from(new SimpleGrantedAuthority("useradmin"))));
    assertThat(DefinedAuthority.AUTHENTICATION_BROKER, equalTo(DefinedAuthority.from(new SimpleGrantedAuthority("authenticationbroker"))));
    assertThat(DefinedAuthority.PATIENT_SOURCE, equalTo(DefinedAuthority.from(new SimpleGrantedAuthority("patientsource"))));
    assertThat(DefinedAuthority.SYSTEM, equalTo(DefinedAuthority.from(new SimpleGrantedAuthority("system"))));
  }

  @Test
  public void matchingEnumFindsEnum()
  {
    assertThat(DefinedAuthority.PROVIDER, equalTo(DefinedAuthority.from(DefinedAuthority.PROVIDER)));
    assertThat(DefinedAuthority.USER_ADMIN, equalTo(DefinedAuthority.from(DefinedAuthority.USER_ADMIN)));
    assertThat(DefinedAuthority.AUTHENTICATION_BROKER, equalTo(DefinedAuthority.from(DefinedAuthority.AUTHENTICATION_BROKER)));
    assertThat(DefinedAuthority.PATIENT_SOURCE, equalTo(DefinedAuthority.from(DefinedAuthority.PATIENT_SOURCE)));
    assertThat(DefinedAuthority.SYSTEM, equalTo(DefinedAuthority.from(DefinedAuthority.SYSTEM)));
  }

  @Test
  public void fromGrantedAuthorityIsCaseInsensitive()
  {
    assertThat(DefinedAuthority.PROVIDER, equalTo(DefinedAuthority.from(new SimpleGrantedAuthority("PrOvIdEr"))));
  }

  @Test
  public void fromCollectionMapsGrantedAuthorityToEnum()
  {
    assertThat(
        Arrays.asList(
            DefinedAuthority.PROVIDER,
            DefinedAuthority.USER_ADMIN,
            DefinedAuthority.PATIENT_SOURCE
        ),
        equalTo(    
            DefinedAuthority.from(
                Arrays.asList(
                    new SimpleGrantedAuthority("provider"),
                    new SimpleGrantedAuthority("useradmin"),
                    new SimpleGrantedAuthority("patientsource")
                )
            )
        )
    );
  }

  @Test
  public void fromCollectionMapsEnumToEnum()
  {
    assertThat(
        Arrays.asList(
            DefinedAuthority.PROVIDER,
            DefinedAuthority.USER_ADMIN,
            DefinedAuthority.PATIENT_SOURCE
        ),
        equalTo(
            DefinedAuthority.from(
                Arrays.asList(
                    DefinedAuthority.PROVIDER,
                    DefinedAuthority.USER_ADMIN,
                    DefinedAuthority.PATIENT_SOURCE
                )
            )
        )
    );
  }

  @Test
  public void isAssignableByUserAdmin() {
    assertThat(DefinedAuthority.PROVIDER.isAssignableByUserAdmin(), is(true));
    assertThat(DefinedAuthority.USER_ADMIN.isAssignableByUserAdmin(), is(true));

    assertThat(DefinedAuthority.AUTHENTICATION_BROKER.isAssignableByUserAdmin(), is(false));
    assertThat(DefinedAuthority.PATIENT_SOURCE.isAssignableByUserAdmin(), is(false));
    assertThat(DefinedAuthority.SYSTEM.isAssignableByUserAdmin(), is(false));
  }

  @Test
  public void getAdminAssignable() {
    for(DefinedAuthority authority : DefinedAuthority.values()) {
      if (authority.isAssignableByUserAdmin()) {
        assertThat(DefinedAuthority.getAdminAssignableAuthorities(), hasItem(authority));
      } else {
        assertThat(DefinedAuthority.getAdminAssignableAuthorities(), not(hasItem(authority)));
      }
    }
  }


}