/* Project: continuum
 * File: PatientLinkProcessor.java
 * Created: Mar 31, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.hypermedia;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;
import org.vumc.controllers.PatientResourceController;
import org.vumc.model.Patient;

import static org.springframework.hateoas.core.DummyInvocationUtils.methodOn;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Component
public class PatientLinkProcessor implements ResourceProcessor<Resource<Patient>>
{
  @Override
  public Resource<Patient> process(final Resource<Patient> resource)
  {
    if (resource.getContent().body != null)
    {
      resource.add(linkTo(
          methodOn(PatientResourceController.class)
              .getHtml(resource.getContent().id))
                       .withRel("content"));
    }
    return resource;
  }


}
