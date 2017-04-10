/* Project: continuum
 * File: Patient.java
 * Created: Mar 03, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class Patient
{
  @Id
  @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="PATIENT_SEQ")
  @SequenceGenerator(name="PATIENT_SEQ",sequenceName="PATIENT_SEQ",allocationSize=1)
  public long          id;

  @CreatedDate
  public ZonedDateTime created;

  @Column(length=30)
  public String      patientId;
  @Embedded
  public PatientName name = new PatientName();
  @Column(length=1)
  public String      gender;

  public LocalDate   dob;

  @Lob
  @JsonIgnore
  public String body;

  @Override
  public String toString()
  {
    return MoreObjects.toStringHelper(this)
               .add("id", id)
               .add("created", created)
               .add("patientId", patientId)
               .toString();
  }

  public long getId()
  {
    return id;
  }

  public void setId(final long inId)
  {
    id = inId;
  }

  public ZonedDateTime getCreated()
  {
    return created;
  }

  public void setCreated(final ZonedDateTime inCreated)
  {
    created = inCreated;
  }

  public String getPatientId()
  {
    return patientId;
  }

  public void setPatientId(final String inPatientId)
  {
    patientId = inPatientId;
  }

  public PatientName getName()
  {
    return name;
  }

  public void setName(final PatientName inName)
  {
    name = inName;
  }

  public String getGender()
  {
    return gender;
  }

  public void setGender(final String inGender)
  {
    gender = inGender;
  }

  public LocalDate getDob()
  {
    return dob;
  }

  public void setDob(final LocalDate inDob)
  {
    dob = inDob;
  }

  public String getBody()
  {
    return body;
  }

  public void setBody(final String inBody)
  {
    body = inBody;
  }

}
