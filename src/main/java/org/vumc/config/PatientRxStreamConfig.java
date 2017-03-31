/* Project: continuum
 * File: NewPatientRxStreamConfig.java
 * Created: Mar 19, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.config;

import com.google.common.base.Charsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.vumc.model.Patient;
import org.vumc.repository.PatientRepository;
import org.vumc.transformations.c32.PatientC32Converter;
import rx.Observable;
import rx.Observer;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;

@Configuration
public class PatientRxStreamConfig
{
  private final PatientC32Converter patientC32Converter;
  private final PatientRepository patientRepository;

  // patient stream
  private Subject<Patient, Patient> patientSubject =
      PublishSubject.<Patient>create().toSerialized();

  // patient C32 input stream
  private Subject<String, String> patientXMLSubject =
      PublishSubject.<String>create().toSerialized();

  @Autowired
  PatientRxStreamConfig(PatientC32Converter patientC32Converter,
                        PatientRepository patientRepository) {
    this.patientC32Converter = patientC32Converter;
    this.patientRepository = patientRepository;
  }

  @Bean(name = "patientObservable")
  Observable<Patient> patientObservable() {
    return patientSubject;
  }

  @Bean(name = "patientXMLObserver")
  Observer<String> patientXMLObserver() {
    return patientXMLSubject;
  }

  @PostConstruct
  void setupC32PatientStream() {
    patientXMLSubject.retry()
        .map(s -> new ByteArrayInputStream(s.getBytes(Charsets.UTF_8)))
        .map(patientC32Converter::convert)
        .map(patientRepository::save)
        .subscribe(patientSubject);
  }

}
