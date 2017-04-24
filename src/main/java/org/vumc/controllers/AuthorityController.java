/* Project: continuum
 * File: AuthorityController.java
 * Created: Apr 24, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vumc.model.DefinedAuthority;
import org.vumc.security.annotations.AllowedAuthorities;

import java.util.List;

@RestController
@RequestMapping("/api/authorities")
@AllowedAuthorities(DefinedAuthority.USER_ADMIN)
public class AuthorityController
{

  @GetMapping
  public List<DefinedAuthority> getAuthorities() throws Exception {
    return DefinedAuthority.getAdminAssignableAuthorities();
  }

}
