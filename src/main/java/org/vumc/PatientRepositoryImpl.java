/* Project: continuum
 * File: PatientRepositoryImpl.java
 * Created: Mar 06, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import rx.Observable;
import rx.Subscription;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Repository
public class PatientRepositoryImpl implements PatientRepository
{
  private List<Patient> patients = new LinkedList<>();

  private Observable<Patient> patientObservable;
  private Subscription        subscription;

  @Autowired
  public PatientRepositoryImpl(
      @Qualifier("patientObservable") Observable<Patient> inPatientObservable)
  {
    patientObservable = inPatientObservable;
  }

  private synchronized void insertPatient(Patient inPatient) {
    patients.add(inPatient);
  }

  @Override
  public synchronized List<? extends Patient> findAll()
  {
    return new ArrayList<>(patients);
  }

  @Override
  public Patient find(final int inId)
  {
    return patients
        .stream()
        .filter(p -> p.id == inId)
        .findFirst()
        .orElse(null);
  }

  @PostConstruct
  void subscribeToPatientFeed() {
    subscription = patientObservable.subscribe(this::insertPatient);
  }

  @PreDestroy
  void unsubscribe() {
    subscription.unsubscribe();
  }

}