/* Project: continuum
 * File: PatientResource.java
 * Created: Mar 03, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.vumc.model.Patient;
import org.vumc.repository.PatientRepository;
import rx.Observer;

import javax.xml.transform.TransformerException;
import java.io.IOException;

@RestController
@RequestMapping("/api/patients")
public class PatientResourceController
{
  private static final Logger LOGGER = LoggerFactory.getLogger(PatientResourceController.class);

  private final Observer<String> patientXMLObserver;
  private final PatientRepository patientRepository;

  @Autowired
  public PatientResourceController(
      @Qualifier("patientXMLObserver") Observer<String> patientXMLObserver,
      PatientRepository patientRepository)
  {
    this.patientXMLObserver = patientXMLObserver;
    this.patientRepository = patientRepository;
  }

  @PreAuthorize("hasAuthority('patientsource')")
  @RequestMapping(path = "c32", method = RequestMethod.POST,
                  consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_XML_VALUE })
  public void postC32Document(@RequestBody String inCdaRequest)
      throws TransformerException, IOException
  {
    patientXMLObserver.onNext(inCdaRequest);
  }


  @PreAuthorize("hasAuthority('provider')")
  @RequestMapping(path = "{id}/body.html", method = RequestMethod.GET,
                  produces = MediaType.TEXT_HTML_VALUE)
  public ResponseEntity<String> getHtml(@PathVariable("id") long id) {
    Patient patient = patientRepository.findOne(id);
    if (patient != null)
    {
      String body = patient.body;
      if (body != null)
      {
        return ResponseEntity.ok(body);
      }
    }
    return ResponseEntity.notFound().build();
  }

}