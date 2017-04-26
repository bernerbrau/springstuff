/* Project: continuum
 * File: LinkSecurityAspect.java
 * Created: Apr 26, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.hypermedia.aop;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.WebInvocationPrivilegeEvaluator;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Aspect
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class LinkSecurityAspect
{
  @Autowired
  @Lazy // NOTE: Accessing this bean in an eager way seems to break the HTTP security configuration horribly.
  private WebInvocationPrivilegeEvaluator webPrivilegeEval;

//  @Autowired
//  @Lazy // NOTE: Accessing this bean in an eager way seems to break the HTTP security configuration horribly.
//  private MethodInvocationPrivilegeEvaluator methodPrivilegeEval;

  @AfterReturning(pointcut="execution(* org.springframework.hateoas.ResourceProcessor+.process(..))",
                  returning="resource")
  public void filterDisallowedLinks(ResourceSupport resource) {
    List<Link> filtered = resource.getLinks().stream()
                              .filter(this::isLinkPermitted)
                              .collect(Collectors.toList());

    resource.removeLinks();
    resource.add(filtered);
  }

  private boolean isLinkPermitted(final Link inLink)
  {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String appRelativeLink = getAppRelativeLink(inLink);
    boolean webInvocationPermitted = webPrivilegeEval.isAllowed(appRelativeLink, authentication);
    return webInvocationPermitted; // && methodInvocationPermitted;
  }

  private String getAppRelativeLink(final Link inLink)
  {
    String base = ServletUriComponentsBuilder.fromCurrentServletMapping().path("").toUriString();
    return inLink.getHref().replace(base,"");
  }
}
