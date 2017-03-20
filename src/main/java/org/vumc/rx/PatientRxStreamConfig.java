/* Project: continuum
 * File: NewPatientRxStreamConfig.java
 * Created: Mar 19, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.rx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.vumc.PatientRepository;
import org.vumc.model.Patient;
import org.vumc.transformations.Transformations;
import rx.Observable;
import rx.Observer;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

import javax.annotation.PostConstruct;

@Configuration
public class PatientRxStreamConfig
{
  private final Transformations transformations;

  // patient stream
  private Subject<Patient, Patient> patientSubject =
      PublishSubject.<Patient>create().toSerialized();

  // raw patient XML stream
  private Subject<String, String> patientXMLSubject =
      PublishSubject.<String>create().toSerialized();

  @Autowired
  PatientRxStreamConfig(Transformations transformations) {
    this.transformations = transformations;
  }

  @Bean(name = "patientObservable")
  Observable<Patient> patientObservable(PatientRepository inRepository) {
    return patientSubject.map(inRepository::save).retry();
  }

  @Bean(name = "patientObserver")
  Observer<Patient> patientObserver() {
    return patientSubject;
  }

  @Bean(name = "patientXMLObserver")
  Observer<String> patientXMLObserver() {
    return patientXMLSubject;
  }

  @PostConstruct
  void feedPatientXMLToPatientStream() {
    patientXMLSubject
        .map(transformations::extractC32Document)
        .map(transformations::c32DocumentToPatientJsonString)
        .map(transformations::checkJsonForErrorResult)
        .map(transformations.jsonToObject(Patient.class)::apply)
        .retry()
        .subscribe(patientSubject);
  }

}
