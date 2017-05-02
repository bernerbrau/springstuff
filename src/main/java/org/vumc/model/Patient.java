/* Project: continuum
 * File: Patient.java
 * Created: Mar 03, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.MoreObjects;

import javax.persistence.*;
import java.sql.Clob;
import java.time.LocalDate;
import java.time.ZonedDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Entity
@JsonInclude(NON_NULL)
public class Patient
{
  @Id
  @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="PATIENT_SEQ")
  @SequenceGenerator(name="PATIENT_SEQ",sequenceName="PATIENT_SEQ",allocationSize=1)
  private long          id;

  @Column(nullable = false)
  private ZonedDateTime created;

  @Column(length=30, nullable=false)
  private String      patientId;
  @Column(length=100, nullable=false)
  private String      idAssigningAuthority;
  @Embedded
  private PatientName name = new PatientName();
  @Column(length=1)
  private String      gender;
  @Column
  private LocalDate   dob;

  @Lob
  @JsonIgnore
  private Clob body;

  @Lob
  @JsonIgnore
  private Clob rawMessage;

  @Override
  public String toString()
  {
    return MoreObjects.toStringHelper(this)
               .add("id", id)
               .add("created", created)
               .add("patientId", patientId)
               .add("idAssigningAuthority", idAssigningAuthority)
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

  public String getIdAssigningAuthority()
  {
    return idAssigningAuthority;
  }

  public void setIdAssigningAuthority(final String inIdAssigningAuthority)
  {
    idAssigningAuthority = inIdAssigningAuthority;
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

  public Clob getBody()
  {
    return body;
  }

  public void setBody(final Clob inBody)
  {
    body = inBody;
  }

  public Clob getRawMessage() {
    return rawMessage;
  }

  public void setRawMessage(final Clob inRawMessage) {
    rawMessage = inRawMessage;
  }

}
