/* Project: continuum
 * File: LinkKludges.java
 * Created: Apr 28, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.hypermedia.kludges;

import org.springframework.hateoas.Link;

public enum LinkKludges
{
  /* No enum members - util class */;

  public static Link fixTemplateParams(final Link inLink)
  {
    return new Link(
       inLink.getHref()
           .replaceAll("%7B(.*?)%7D","{$1}"),
       inLink.getRel()
    );
  }
}
