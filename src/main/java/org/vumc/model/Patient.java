/* Project: continuum
 * File: Patient.java
 * Created: Mar 03, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.ZonedDateTime;

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
  public PatientName name;
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
               .add("name", name)
               .add("gender", gender)
               .add("dob", dob)
               .add("body", body)
               .toString();
  }

}
