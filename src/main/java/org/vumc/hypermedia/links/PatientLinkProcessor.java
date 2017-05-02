/* Project: continuum
 * File: PatientLinkProcessor.java
 * Created: Mar 31, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.hypermedia.links;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;
import org.vumc.controllers.PatientResourceController;
import org.vumc.model.Patient;

import java.io.IOException;
import java.sql.SQLException;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class PatientLinkProcessor implements ResourceProcessor<Resource<Patient>>
{
  @Override
  public Resource<Patient> process(final Resource<Patient> resource)
  {
    Patient patient = resource.getContent();
    try
    {
      if (patient.getBody() != null && patient.getBody().length() != 0)
      {
        resource.add(linkTo(
            methodOn(PatientResourceController.class)
                .getHtml(patient.getId()))
                         .withRel("content"));
      }
    }
    catch (IOException | SQLException inE)
    {
      // If error reading CLOB stream, don't present a link.
    }
    return resource;
  }


}
