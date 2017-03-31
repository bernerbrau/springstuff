/* Project: continuum
 * File: ApiLinksProcessor.java
 * Created: Mar 30, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.hypermedia;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.rest.webmvc.RepositoryLinksResource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.vumc.controllers.LoginController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Component
@Order(0)
public class ApiLinksProcessor implements ResourceProcessor<RepositoryLinksResource>
{
  @Autowired
  public ApiLinksProcessor()
  {
  }

  @Override
  public RepositoryLinksResource process(final RepositoryLinksResource resource)
  {
    if (SecurityContextHolder.getContext()
            .getAuthentication() instanceof AnonymousAuthenticationToken)
    {
      resource.add(linkTo(LoginController.class).withRel("login"));
    }
    return resource;
  }

}
