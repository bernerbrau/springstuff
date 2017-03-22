/* Project: continuum
 * File: Patient.java
 * Created: Mar 03, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.model;

import com.google.common.base.MoreObjects;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class Patient
{
  @Id
  @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="PATIENT_SEQ")
  @SequenceGenerator(name="PATIENT_SEQ",sequenceName="PATIENT_SEQ",allocationSize=1)
  public long        id;
  @Column(length=30)
  public String      patientId;
  @Embedded
  public PatientName name;
  @Column(length=1)
  public String      gender;

  public LocalDate   dob;
  @Lob
  public String      body;

  public Patient() {}

  @Override
  public String toString()
  {
    return MoreObjects.toStringHelper(this)
               .add("id", id)
               .add("patientId", patientId)
               .add("name", name)
               .add("gender", gender)
               .add("dob", dob)
               .add("body", body)
               .toString();
  }

}
