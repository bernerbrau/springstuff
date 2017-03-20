/* Project: continuum
 * File: PatientResource.java
 * Created: Mar 03, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Streams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.vumc.hypermedia.PatientResourceAssembler;
import org.vumc.hypermedia.resources.PatientResource;
import org.vumc.model.Patient;
import org.vumc.transformations.Transformations;
import rx.Observer;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@CrossOrigin(origins = "*")
@ExposesResourceFor(PatientResource.class)
@RequestMapping("/api/patients")
public class PatientResourceController
{

  private final PatientRepository        patientRepository;
  private final Observer<Patient>        patientObserver;
  private final Observer<String>         patientXMLObserver;
  private final PatientResourceAssembler assembler;

  @Autowired
  public PatientResourceController(PatientRepository patientRepository,
                                   PatientResourceAssembler patientResourceAssembler,
                                   @Qualifier("patientObserver") Observer<Patient> patientObserver,
                                   @Qualifier("patientXMLObserver") Observer<String> patientXMLObserver)
  {
    this.patientRepository = patientRepository;
    this.patientObserver = patientObserver;
    this.patientXMLObserver = patientXMLObserver;
    this.assembler = patientResourceAssembler;
  }

  @RequestMapping(method = RequestMethod.GET)
  public List<? extends PatientResource> getPatientList() {
    Iterable<Patient> patients = patientRepository.findAll();
    return Streams.stream(patients)
               .map(assembler::toResource)
               .collect(Collectors.toList());
  }

//  @RequestMapping(method = RequestMethod.GET)
//  public List<? extends PatientResource> catchUp(@RequestParam("since") int latestId)
//  {
//    return patientRepository.findByIdGreaterThan(latestId);
//  }

  @RequestMapping(path = "{id}", method = RequestMethod.GET)
  public PatientResource getPatient(@PathVariable long id) {
    return assembler.toResource(patientRepository.findOne(id));
  }

  @RequestMapping(path = "{id}.html", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE )
  public CharSequence getPatientHtml(@PathVariable long id) {
    return patientRepository.findOne(id).body;
  }

  @RequestMapping(method = RequestMethod.POST)
  public void insertPatient(@RequestBody Patient inPatient) {
    patientObserver.onNext(inPatient);
  }

  @RequestMapping(path = "c32", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_XML_VALUE })
  public void insertPatient(@RequestBody String inCdaRequest)
      throws TransformerException, IOException
  {
    patientXMLObserver.onNext(inCdaRequest);
  }

}