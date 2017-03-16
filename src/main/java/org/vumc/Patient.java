/* Project: continuum
 * File: Patient.java
 * Created: Mar 03, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.base.MoreObjects;

public class Patient
{
  @JsonView(View.Summary.class)
  public int         _id;
  @JsonView(View.Summary.class)
  public PatientId   id;
  @JsonView(View.Summary.class)
  public PatientName name;
  @JsonView(View.Summary.class)
  public String      gender;
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
