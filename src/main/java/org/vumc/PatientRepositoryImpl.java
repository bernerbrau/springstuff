/* Project: continuum
 * File: PatientRepositoryImpl.java
 * Created: Mar 06, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc;

import com.google.common.collect.Ordering;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import rx.Observable;
import rx.Subscription;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PatientRepositoryImpl implements PatientRepository
{

  private List<Patient>       patients;
  private Observable<Patient> patientObservable;
  private Subscription        subscription;

  @Autowired
  public PatientRepositoryImpl(
      @Qualifier("patientObservable") Observable<Patient> inPatientObservable,
      @Qualifier("patientPersistentList") List<Patient> inPatients)
  {
    patientObservable = inPatientObservable;
    patients = inPatients;
  }

  @Transactional
  private void insertPatient(Patient inPatient)
  {
    patients.add(inPatient);
  }

  @Transactional
  @Override
  public List<? extends Patient> findAll()
  {
    return new ArrayList<>(patients);
  }

  @Transactional
  @Override
  public Patient find(final int inId)
  {
    return patients.stream().filter(p -> p.id == inId).findFirst().orElse(null);
  }

  public int getNextId() {
    return patients.stream().map(p -> p.id).max(Ordering.natural()).orElse(0) + 1;
  }

  @PostConstruct
  void subscribeToPatientFeed()
  {
    subscription = patientObservable.subscribe(this::insertPatient);
  }

  @PreDestroy
  void unsubscribe()
  {
    subscription.unsubscribe();
  }

}