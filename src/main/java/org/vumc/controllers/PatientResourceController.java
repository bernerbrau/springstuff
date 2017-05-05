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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vumc.model.DefinedAuthority;
import org.vumc.model.Patient;

import org.vumc.repository.PatientRepository;
import org.vumc.security.annotations.AllowedAuthorities;
import org.vumc.transformations.c32.PatientC32Converter;

import org.vumc.repository.RawC32Repository;
import org.vumc.transformations.c32.RawC32RecordCreator;

import com.google.common.io.CharStreams;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;

@RestController
@RequestMapping("/api/patients")
public class PatientResourceController
{
  private static final Logger LOGGER = LoggerFactory.getLogger(PatientResourceController.class);

  private final PatientC32Converter patientC32Converter;
  private final PatientRepository patientRepository;
  private final MessageChannel newPatients;
  private final RawC32RecordCreator rawC32RecordCreator;
  private final RawC32Repository rawC32Repository;

  @Autowired
  public PatientResourceController(
      PatientC32Converter patientC32Converter,
      PatientRepository patientRepository,
      @Qualifier("newPatients")
      final MessageChannel inNewPatients,
      RawC32RecordCreator inRawC32RecordCreator,
      RawC32Repository inRawC32Repository)
  {
    this.patientC32Converter = patientC32Converter;
    this.patientRepository = patientRepository;
    this.newPatients = inNewPatients;
    this.rawC32RecordCreator = inRawC32RecordCreator;
    this.rawC32Repository = inRawC32Repository;
  }

  @AllowedAuthorities(DefinedAuthority.PATIENT_SOURCE)
  @PostMapping(path = "c32",
                  consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_XML_VALUE })
  public void postC32Document(@RequestBody String inC32Request)
      throws TransformerException, IOException
  {
    LOGGER.info("Received new c32");
    LOGGER.debug("Message content: {}", inC32Request);

    //---  stuff inC32Request into table RAW_MESSAGE.
    rawC32Repository.save(rawC32RecordCreator.createRawC32MessageObject(inC32Request));

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

  @AllowedAuthorities(DefinedAuthority.PROVIDER)
  @GetMapping(path = "{id}/body.html", produces = MediaType.TEXT_HTML_VALUE)
  public ResponseEntity<String> getHtml(@PathVariable("id") long id) throws IOException,
                                                                            SQLException {
    Patient patient = patientRepository.findOne(id);
    if (patient != null)
    {
      Reader body = patient.getBody().getCharacterStream();
      if (body != null)
      {
        LOGGER.info("Presented patient with id {} to user.", id);
        return ResponseEntity.ok(CharStreams.toString(body));
      }
    }
    LOGGER.info("Patient with id {} not found.", id);
    return ResponseEntity.notFound().build();
  }

}