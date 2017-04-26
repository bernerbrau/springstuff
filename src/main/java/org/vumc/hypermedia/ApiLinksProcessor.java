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
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.vumc.controllers.LoginController;
import org.vumc.model.DefinedAuthority;
import org.vumc.model.Patient;
import org.vumc.model.User;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Component
@Order(0)
public class ApiLinksProcessor implements ResourceProcessor<RepositoryLinksResource>
{
  private final EntityLinks        entityLinks;
  private final UserDetailsService userDetailsService;

  @Autowired
  public ApiLinksProcessor(EntityLinks inEntityLinks, final UserDetailsService inUserDetailsService)
  {
    this.entityLinks = inEntityLinks;
    this.userDetailsService = inUserDetailsService;
  }

  @Override
  public RepositoryLinksResource process(final RepositoryLinksResource resource)
  {
    // Strip out the default links.
    resource.removeLinks();

    ifAnonymous(() ->
      resource.add(linkTo(LoginController.class).withRel("login"))
    );

    ifHasAuthority(DefinedAuthority.PROVIDER, () ->
      resource.add(entityLinks.linkToCollectionResource(Patient.class))
    );

    ifHasAuthority(DefinedAuthority.USER_ADMIN, () -> {
      resource.add(entityLinks.linkToCollectionResource(User.class).withRel("users"));
      resource.add(entityLinks.linkToCollectionResource(DefinedAuthority.class).withRel("authorities"));
    });

    return resource;
  }

  public void ifAnonymous(Runnable r) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth instanceof AnonymousAuthenticationToken) {
      r.run();
    }
  }

  public void ifHasAuthority(DefinedAuthority required, Runnable r) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    UserDetails details = userDetailsService.loadUserByUsername(auth.getName());
    if (details.getAuthorities().stream().anyMatch(a -> required.getAuthority().equalsIgnoreCase(a.getAuthority()))) {
      r.run();
    }
  }

}
