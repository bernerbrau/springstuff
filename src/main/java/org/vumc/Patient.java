/* Project: continuum
 * File: Patient.java
 * Created: Mar 03, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc;

import com.google.common.base.MoreObjects;

public class Patient
{
  public int         _id;
  public PatientId   id;
  public PatientName name;
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
