/* Project: continuum
 * File: LoginController.java
 * Created: Mar 30, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.vumc.model.UsernameAndPassword;

@RestController
@RequestMapping("/api/login")
public class LoginController
{
  private final AuthenticationManager authManager;

  @Autowired
  public LoginController(final AuthenticationManager inAuthManager)
  {
    authManager = inAuthManager;
  }

  @RequestMapping(method = RequestMethod.POST)
  public void login(@RequestBody UsernameAndPassword credentials) throws AuthenticationException
  {
    SecurityContextHolder.getContext()
        .setAuthentication(
            authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    credentials.username,
                    credentials.password)));
  }
}
