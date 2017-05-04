/* Project: continuum
 * File: AuthorityControllerTest.java
 * Created: May 04, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.controllers;

import org.junit.Before;
import org.junit.Test;
import org.vumc.model.DefinedAuthority;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class AuthorityControllerTest
{
  private AuthorityController controller;

  @Before
  public void setUp() {
    controller = new AuthorityController();
  }

  @Test
  public void getAuthoritiesReturnsAdminAssignableAuthorities() throws Exception {
    for(DefinedAuthority authority : controller.getAuthorities()) {
      if (authority.isAssignableByUserAdmin()) {
        assertThat(DefinedAuthority.getAdminAssignableAuthorities(), hasItem(authority));
      } else {
        assertThat(DefinedAuthority.getAdminAssignableAuthorities(), not(hasItem(authority)));
      }
    }
  }

}
