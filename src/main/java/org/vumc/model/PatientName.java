/* Project: continuum
 * File: PatientId.java
 * Created: Mar 16, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.model;

import com.google.common.base.MoreObjects;

public class PatientName
{
  public String family;
  public String given;
  public String suffix;

  @Override
  public String toString()
  {
    return MoreObjects.toStringHelper(this)
               .add("family", family)
               .add("given", given)
               .add("suffix", suffix)
               .toString();
  }
}
