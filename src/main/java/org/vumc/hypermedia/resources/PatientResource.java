/* Project: continuum
 * File: Patient.java
 * Created: Mar 03, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.hypermedia.resources;

import com.google.common.base.MoreObjects;
import org.springframework.hateoas.ResourceSupport;
import org.vumc.model.PatientId;
import org.vumc.model.PatientName;

import javax.persistence.*;
import java.time.LocalDate;

public class PatientResource extends ResourceWithEmbeddeds
{
  public long        _id;
  public PatientId   id;
  public PatientName name;
  public String      gender;
  public LocalDate   dob;

  public PatientResource() {}

  @Override
  public String toString()
  {
    return MoreObjects.toStringHelper(this)
               .add("_id", _id)
               .add("id", id)
               .add("name", name)
               .add("gender", gender)
               .add("dob", dob)
               .toString();
  }

}
