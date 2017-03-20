/* Project: continuum
 * File: Patient.java
 * Created: Mar 03, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.model;

import com.google.common.base.MoreObjects;
import org.vumc.hypermedia.resources.PatientResource;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class Patient
{
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  public long        _id;
  @Embedded
  public PatientId   id;
  @Embedded
  public PatientName name;
  public String      gender;
  public LocalDate   dob;
  @Lob
  public String      body;

  public Patient() {}

  @Override
  public String toString()
  {
    return MoreObjects.toStringHelper(this)
               .add("_id", _id)
               .add("id", id)
               .add("name", name)
               .add("gender", gender)
               .add("body", body)
               .toString();
  }

}
