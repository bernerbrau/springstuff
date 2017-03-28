/* Project: continuum
 * File: PatientResource.java
 * Created: Mar 03, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.controllers;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v22.datatype.PN;
import ca.uhn.hl7v2.model.v22.message.ADT_A04;
import ca.uhn.hl7v2.model.v22.segment.PID;
import ca.uhn.hl7v2.parser.CanonicalModelClassFactory;
import ca.uhn.hl7v2.parser.PipeParser;
import com.google.common.collect.Streams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.vumc.repository.PatientRepository;
import org.vumc.hypermedia.PatientResourceAssembler;
import org.vumc.hypermedia.resources.PatientResource;
import org.vumc.model.Patient;
import org.vumc.model.PatientName;
import rx.Observer;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static ca.uhn.hl7v2.model.primitive.CommonTM.GMT_OFFSET_NOT_SET_VALUE;

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

  @RequestMapping(path = "hl7", method = RequestMethod.POST,
                  consumes = "x-application/hl7-v2+er7",
                  produces = "x-application/hl7-v2+er7")
  public String insertPatientViaHL7(@RequestBody String inHl7Message) throws Exception
  {
    // Corrections to make HAPI happy
    inHl7Message = inHl7Message
                       .replaceAll(
                           "\\b(\\d{4}[01]\\d\\d{2}[012]\\d[0-5]\\d[0-5]\\d)(\\d{1,4}([+\\-]\\d{4})?)\\b",
                           "$1.$2")
                       .replaceAll("(\\((\\d{3})\\)-?|\\b(\\d{3})-)(\\d{3})-(\\d{4})\\b",
                           "($2$3)$4-$5");
    HapiContext context = new DefaultHapiContext();

    CanonicalModelClassFactory mcf = new CanonicalModelClassFactory("2.2");
    context.setModelClassFactory(mcf);

    PipeParser parser = context.getPipeParser();
    Message msg = parser.parse(inHl7Message);

    if (msg instanceof ADT_A04)
    {
      ADT_A04 a04 = (ADT_A04) msg;
      Patient p = new Patient();

      PID pid = a04.getPID();
      PN patientName = pid.getPatientName();

      p.patientId = pid.getPatientIDInternalID(0).getIDNumber().getValue();

      p.name = new PatientName();
      p.name.family = patientName.getFamilyName().getValue();
      p.name.given = patientName.getGivenName().getValue();
      p.name.suffix = patientName.getPn4_SuffixEgJRorIII().getValue();

      p.gender = pid.getSex().getValue();

      Date dobAsUtilDate = pid.getDateOfBirth().getTimeOfAnEvent().getValueAsDate();
      int tzOffset =
          pid.getDateOfBirth().getTimeOfAnEvent().getGMTOffset();
      ZoneId zoneId;
      if (tzOffset == GMT_OFFSET_NOT_SET_VALUE)
      {
        zoneId = ZoneId.systemDefault();
      }
      else
      {
        zoneId = ZoneId.of(String.format("GMT%+05d", tzOffset));
      }
      p.dob = LocalDateTime.ofInstant(dobAsUtilDate.toInstant(), zoneId).toLocalDate();

      patientObserver.onNext(p);
    }

    return msg.generateACK().encode();
  }

}