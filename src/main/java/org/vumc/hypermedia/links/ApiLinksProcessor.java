/* Project: continuum
 * File: ApiLinksProcessor.java
 * Created: Mar 30, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.hypermedia.links;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.rest.webmvc.RepositoryLinksResource;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.hateoas.TemplateVariable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.vumc.controllers.ClientLogController;
import org.vumc.controllers.LoginController;
import org.vumc.controllers.UserController;
import org.vumc.model.DefinedAuthority;
import org.vumc.model.Patient;
import org.vumc.model.User;

import java.lang.reflect.Method;

import static org.springframework.hateoas.TemplateVariable.VariableType.PATH_VARIABLE;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.vumc.hypermedia.kludges.LinkKludges.fixTemplateParams;

@Component
@Order(0)
public class ApiLinksProcessor implements ResourceProcessor<RepositoryLinksResource>
{
  private final EntityLinks        entityLinks;
  private final RepositoryEntityLinks repositoryEntityLinks;
  private final UserDetailsService userDetailsService;

  private static final Method USER_LOOKUP_METHOD_REF;

  static
  {
    try
    {
      USER_LOOKUP_METHOD_REF = UserController.class.getDeclaredMethod("getUser", String.class);
    }
    catch (NoSuchMethodException e)
    {
      throw new ExceptionInInitializerError(e);
    }
  }

  @Autowired
  public ApiLinksProcessor(final EntityLinks inEntityLinks,
                           final RepositoryEntityLinks inRepositoryEntityLinks,
                           final UserDetailsService inUserDetailsService)
  {
    this.entityLinks = inEntityLinks;
    this.repositoryEntityLinks = inRepositoryEntityLinks;
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

    ifAuthenticated(() ->
      resource.add(linkTo(ClientLogController.class).withRel("log"))
    );

    ifHasAuthority(DefinedAuthority.PROVIDER, () -> {
      resource.add(repositoryEntityLinks.linkToCollectionResource(Patient.class));
      resource.add(
          fixTemplateParams(
              repositoryEntityLinks.linkToSingleResource(
                  Patient.class,
                  new TemplateVariable("id", PATH_VARIABLE)
              ).withRel("patientTemplate")
          ));
      resource.add(repositoryEntityLinks.linksToSearchResources(Patient.class));
    });

    ifHasAuthority(DefinedAuthority.USER_ADMIN, () -> {
      resource.add(entityLinks.linkToCollectionResource(User.class).withRel("users"));
      resource.add(
          fixTemplateParams(
              entityLinks.linkToSingleResource(
                  User.class,
                  new TemplateVariable("username", PATH_VARIABLE)
              ).withRel("userTemplate")
          ));
      resource.add(
          entityLinks.linkToSingleResource(User.class,
              SecurityContextHolder.getContext().getAuthentication().getName()
          ).withRel("myProfile")
      );
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

  public void ifAuthenticated(Runnable r) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (!(auth instanceof AnonymousAuthenticationToken) && auth.isAuthenticated()) {
      r.run();
    }
  }


  public void ifHasAuthority(DefinedAuthority required, Runnable r) {
    try
    {
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      UserDetails details = userDetailsService.loadUserByUsername(auth.getName());
      if (details.getAuthorities().stream()
              .anyMatch(a -> required.getAuthority().equalsIgnoreCase(a.getAuthority())))
      {
        r.run();
      }
    } catch (AuthenticationException ex) {
      // Safe to ignore this - just assume the authority is not present.
    }
  }

}
