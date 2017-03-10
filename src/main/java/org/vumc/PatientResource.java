/* Project: continuum
 * File: PatientResource.java
 * Created: Mar 03, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import rx.Observer;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/patients")
public class PatientResource
{
  private final AtomicInteger sequence;
  private final PatientRepository patientRepository;
  private final Observer<Patient> patientObserver;

  @Autowired
  public PatientResource(PatientRepository patientRepository,
                         Observer<Patient> patientObserver) {
    this.sequence = new AtomicInteger(patientRepository.getNextId());
    this.patientRepository = patientRepository;
    this.patientObserver = patientObserver;
  }

  @RequestMapping(method = RequestMethod.GET)
  public List<? extends Patient> getPatientList() {
    return patientRepository.findAll();
  }

  @RequestMapping(path = "{id}", method = RequestMethod.GET)
  public Patient getPatient(@PathVariable int id) {
    return patientRepository.find(id);
  }

  @RequestMapping(method = RequestMethod.POST)
  public void insertPatient(@RequestBody Patient inPatient) {
    inPatient.id = this.sequence.incrementAndGet();
    patientObserver.onNext(inPatient);
  }

}
