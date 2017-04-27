/* Project: continuum
 * File: UserResourceAssembler.java
 * Created: Apr 27, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.hypermedia.assembler;

import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.vumc.controllers.UserController;
import org.vumc.model.User;

@Component
public class UserResourceAssembler extends ResourceAssemblerSupport<UserDetails, User>
{
  public UserResourceAssembler()
  {
    super(UserController.class, User.class);
  }

  @Override
  public User toResource(final UserDetails entity)
  {
    return createResourceWithId(entity.getUsername(), entity);
  }

  @Override
  protected User instantiateResource(final UserDetails entity)
  {
    return User.fromUserDetails(entity, false);
  }
}
