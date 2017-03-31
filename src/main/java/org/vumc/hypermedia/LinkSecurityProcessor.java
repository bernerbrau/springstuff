/* Project: continuum
 * File: ApiLinksProcessor.java
 * Created: Mar 30, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.hypermedia;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.WebInvocationPrivilegeEvaluator;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class LinkSecurityProcessor<R extends ResourceSupport> implements ResourceProcessor<R>
{
  @Autowired
  @Lazy // NOTE: Accessing this bean in an eager way seems to break the HTTP security configuration horribly.
  private WebInvocationPrivilegeEvaluator privilegeEval;

  @Override
  public R process(final R resource)
  {
    List<Link> filtered = resource.getLinks().stream()
        .filter(this::isLinkPermitted)
        .collect(Collectors.toList());

    resource.removeLinks();
    resource.add(filtered);

    return resource;
  }

  private boolean isLinkPermitted(final Link inLink)
  {
    return privilegeEval.isAllowed(getAppRelativeLink(inLink), SecurityContextHolder.getContext().getAuthentication());
  }

  private String getAppRelativeLink(final Link inLink)
  {
    String base = ServletUriComponentsBuilder.fromCurrentServletMapping().path("").toUriString();
    return inLink.getHref().replace(base,"");
  }
}
