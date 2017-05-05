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
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vumc.model.DefinedAuthority;
import org.vumc.model.Patient;

import org.vumc.model.RawMessage;
import org.vumc.repository.PatientRepository;
import org.vumc.security.annotations.AllowedAuthorities;
import org.vumc.transformations.c32.PatientC32Converter;

import org.vumc.repository.RawC32Repository;

import com.google.common.io.CharStreams;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.SQLException;

@RestController
@RequestMapping("/api/patients")
public class PatientResourceController
{
  private static final Logger LOGGER = LoggerFactory.getLogger(PatientResourceController.class);

  private final PatientC32Converter patientC32Converter;
  private final PatientRepository patientRepository;
  private final RawC32Repository rawC32Repository;
  private final MessageChannel rawC32;

  @Autowired
  public PatientResourceController(
      PatientC32Converter patientC32Converter,
      PatientRepository patientRepository,
      RawC32Repository inRawC32Repository,
      @Qualifier("rawC32")
      final MessageChannel inRawC32)
  {
    this.patientC32Converter = patientC32Converter;
    this.patientRepository = patientRepository;
    this.rawC32Repository = inRawC32Repository;
    this.rawC32 = inRawC32;
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
    rawC32.send(
        new GenericMessage<>(
            rawC32Repository.save(
                RawMessage.create(inC32Request)
            )
        )
    );
  }

  @ServiceActivator(inputChannel = "rawC32", outputChannel = "newPatients")
  public @Payload Patient handleRawC32(@Payload RawMessage rawMessage) {
    try {
      Patient patient = patientRepository.save(
          patientC32Converter.convert(
              rawMessage.getRawMessage()
          )
      );
      rawMessage.updateAccessed();
      rawMessage.setProcessedStatus();
      return patient;
    } catch (Throwable t) {
      rawMessage.setErrorStatus();
      LOGGER.warn("Error handling raw message with id " + rawMessage.getId(), t);
      return null;
    } finally {
      rawMessage.incrementProcessTries();
      rawC32Repository.save(rawMessage);
    }
  }

  @AllowedAuthorities(DefinedAuthority.PROVIDER)
  @GetMapping(path = "{id}/body.html", produces = MediaType.TEXT_HTML_VALUE)
  public ResponseEntity<String> getHtml(@PathVariable("id") long id) throws IOException,
                                                                            SQLException {
    Patient patient = patientRepository.findOne(id);
    if (patient != null)
    {
      Clob clob = patient.getBody();
      if (clob != null)
      {
        Reader body = clob.getCharacterStream();
        LOGGER.info("Presented patient with id {} to user.", id);
        return ResponseEntity.ok(CharStreams.toString(body));
      }
    }
    LOGGER.info("Patient with id {} not found.", id);
    return ResponseEntity.notFound().build();
  }

}