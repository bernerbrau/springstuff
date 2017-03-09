/* Project: continuum
 * File: Patient.java
 * Created: Mar 03, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Patient
{
  public int                id;
  public String             mrn;
  public String             firstName;
  public String             lastName;
  public boolean            hasHieData;
  public Map<String,Object> fields;

  public Patient() {}

  @Override
  public String toString()
  {
    return com.google.common.base.MoreObjects.toStringHelper(this)
               .add("id", id)
               .add("mrn", mrn)
               .add("firstName", firstName)
               .add("lastName", lastName)
               .add("hasHieData", hasHieData)
               .add("fields", fields)
               .toString();
  }
}
