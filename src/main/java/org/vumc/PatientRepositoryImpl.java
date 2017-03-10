/* Project: continuum
 * File: PatientRepositoryImpl.java
 * Created: Mar 06, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Ordering;
import org.jetbrains.annotations.NotNull;
import org.mapdb.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import rx.Observable;
import rx.Subscription;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PatientRepositoryImpl implements PatientRepository
{
  private Serializer<Patient> serializer = new Serializer<Patient>()
  {
    ObjectMapper mapper = new ObjectMapper();

    @Override
    public void serialize(@NotNull final DataOutput2 out, @NotNull final Patient value)
        throws IOException
    {
      mapper.writeValue((DataOutput) out, value);
    }

    @Override
    public Patient deserialize(@NotNull final DataInput2 input, final int available)
        throws IOException
    {
      return mapper.readValue(input, Patient.class);
    }
  };

  private DB patientDB = DBMaker.fileDB("patients.db")
                             .checksumHeaderBypass()
                             .transactionEnable()
                             .make();

  private List<Patient> patients = patientDB
          .indexTreeList("patients", serializer)
          .createOrOpen();

  private Observable<Patient> patientObservable;
  private Subscription        subscription;

  @Autowired
  public PatientRepositoryImpl(
      @Qualifier("patientObservable") Observable<Patient> inPatientObservable)
  {
    patientObservable = inPatientObservable;
  }

  private synchronized void insertPatient(Patient inPatient)
  {
    patients.add(inPatient);
    patientDB.commit();
  }

  @Override
  public synchronized List<? extends Patient> findAll()
  {
    return new ArrayList<>(patients);
  }

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
    patientDB.close();
  }

}