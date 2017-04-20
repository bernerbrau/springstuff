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
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.vumc.model.Patient;
import org.vumc.repository.PatientRepository;
import org.vumc.transformations.c32.PatientC32Converter;

import javax.xml.transform.TransformerException;
import java.io.IOException;

@RestController
@RequestMapping("/api/patients")
public class PatientResourceController
{
  private static final Logger LOGGER = LoggerFactory.getLogger(PatientResourceController.class);

  private final PatientC32Converter patientC32Converter;
  private final PatientRepository patientRepository;
  private final MessageChannel newPatients;

  @Autowired
  public PatientResourceController(
      PatientC32Converter patientC32Converter,
      PatientRepository patientRepository,
      @Qualifier("newPatients")
      final MessageChannel inNewPatients)
  {
    this.patientC32Converter = patientC32Converter;
    this.patientRepository = patientRepository;
    this.newPatients = inNewPatients;
  }

  @PreAuthorize("hasAuthority('patientsource')")
  @RequestMapping(path = "c32", method = RequestMethod.POST,
                  consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_XML_VALUE })
  public void postC32Document(@RequestBody String inC32Request)
      throws TransformerException, IOException
  {
    newPatients.send(
        new GenericMessage<>(
            patientRepository.save(
                patientC32Converter.convert(
                    inC32Request
                )
            )
        )
    );
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