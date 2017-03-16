/* Project: continuum
 * File: PatientResource.java
 * Created: Mar 03, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.w3c.dom.Document;
import rx.Observer;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/patients")
public class PatientResource
{
  private final AtomicInteger     sequence;
  private final PatientRepository patientRepository;
  private final Observer<Patient> patientObserver;
  private final Transformer       cdaTransformer;
  private final ObjectMapper      objectMapper;

  @Autowired
  public PatientResource(PatientRepository patientRepository,
                         Observer<Patient> patientObserver,
                         ObjectMapper objectMapper,
                         @Qualifier("cdaTransformer") Transformer cdaTransformer)
  {
    this.sequence = new AtomicInteger(patientRepository.getNextId());
    this.patientRepository = patientRepository;
    this.patientObserver = patientObserver;
    this.objectMapper = objectMapper;
    this.cdaTransformer = cdaTransformer;
  }

  @JsonView(View.Summary.class)
  @RequestMapping(method = RequestMethod.GET)
  public List<? extends Patient> getPatientList() {
    return patientRepository.findAll();
  }
//
//  @RequestMapping(method = RequestMethod.GET)
//  public List<? extends Patient> catchUp(@RequestParam("since") int latestId)
//  {
//    return patientRepository.findByIdGreaterThan(latestId);
//  }

  @JsonView(View.Summary.class)
  @RequestMapping(path = "{id}", method = RequestMethod.GET)
  public Patient getPatient(@PathVariable int id) {
    return patientRepository.find(id);
  }

  @RequestMapping(path = "{id}.html", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE )
  public String getPatientHtml(@PathVariable int id) {
    return patientRepository.find(id).body;
  }

  @RequestMapping(method = RequestMethod.POST)
  public void insertPatient(@RequestBody Patient inPatient) {
    inPatient._id = this.sequence.incrementAndGet();
    patientObserver.onNext(inPatient);
  }

  @JsonView(View.Summary.class)
  @RequestMapping(path = "cda", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_XML_VALUE })
  public Patient insertPatient(HttpServletRequest inCdaRequest)
      throws TransformerException, IOException
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    cdaTransformer.transform(
        new StreamSource(inCdaRequest.getInputStream()),
        new StreamResult(baos));

    baos.flush();

    Patient patient = objectMapper.readValue(baos.toByteArray(), Patient.class);
    insertPatient(patient);
    return patient;
  }
}