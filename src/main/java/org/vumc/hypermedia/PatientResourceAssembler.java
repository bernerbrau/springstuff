/* Project: continuum
 * File: PatientResourceAssembler.java
 * Created: Mar 20, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.hypermedia;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RelProvider;
import org.springframework.stereotype.Component;
import org.vumc.controllers.PatientResourceController;
import org.vumc.hypermedia.resources.PatientResource;
import org.vumc.model.Patient;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class PatientResourceAssembler extends EmbeddableResourceAssemblerSupport<Patient, PatientResource, PatientResourceController>
{

  @Autowired
  public PatientResourceAssembler(final EntityLinks entityLinks,
                                  final RelProvider relProvider)
  {
    super(entityLinks, relProvider, PatientResourceController.class, PatientResource.class);
  }

  @Override
  public Link linkToSingleResource(final Patient patient)
  {
    return entityLinks.linkToSingleResource(PatientResource.class, patient.id);
  }

  @Override
  public PatientResource toResource(final Patient patient)
  {
    final PatientResource patientRes = createResourceWithId(patient.id, patient);
    patientRes.add(linkTo(methodOn(PatientResourceController.class).getPatientHtml(patient.id)).withRel("content"));
    return patientRes;
  }

  @Override
  protected PatientResource instantiateResource(Patient patient) {
    PatientResource patientRes = new PatientResource();
    patientRes.id = patient.id;
    patientRes.patientId = patient.patientId;
    patientRes.name = patient.name;
    patientRes.gender = patient.gender;
    patientRes.dob = patient.dob;
    return patientRes;
  }

}
