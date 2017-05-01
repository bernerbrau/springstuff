/* Project: continuum
 * File: LoginControllerTest.java
 * Created: May 01, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.controllers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.vumc.model.DefinedAuthority;
import org.vumc.model.UsernameAndPassword;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class LoginControllerTest
{
  private LoginController       controller;
  private AuthenticationManager authMgr;

  @Before
  public void setUp() {
    authMgr = a -> { throw new BadCredentialsException("Default"); };
    controller = new LoginController(a -> authMgr.authenticate(a));
  }

  @Test
  public void testSuccessfulLogin() {
    authMgr = a ->
          new UsernamePasswordAuthenticationToken("testuser", null,
              Collections.singletonList(DefinedAuthority.PROVIDER));

    controller.login(new UsernameAndPassword("testuser","testpass"));

    Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
    assertEquals("testuser", currentAuthentication.getName());
    assertEquals(Collections.singletonList(DefinedAuthority.PROVIDER), currentAuthentication.getAuthorities());
  }

  @Test(expected = BadCredentialsException.class)
  public void testLoginFailure() {
    try
    {
      controller.login(new UsernameAndPassword("testuser", "testpass"));
    }
    finally
    {
      assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
  }

  @After
  public void cleanUp() {
    SecurityContextHolder.clearContext();
  }

}
