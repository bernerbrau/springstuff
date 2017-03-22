/* Project: continuum
 * File: Patient.java
 * Created: Mar 03, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.hypermedia.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import org.vumc.model.PatientName;

import javax.persistence.Embedded;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

public class PatientResource extends ResourceWithEmbeddeds
{
  @JsonProperty("_id")
  public long        id;
  public String      patientId;
  public PatientName name;
  public String      gender;
  public LocalDate   dob;

  @Override
  public String toString()
  {
    return MoreObjects.toStringHelper(this)
               .add("id", id)
               .add("patientId", patientId)
               .add("name", name)
               .add("gender", gender)
               .add("dob", dob)
               .add("links", getLinks())
               .toString();
  }

}
