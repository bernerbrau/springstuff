/* Project: continuum
 * File: PatientId.java
 * Created: Mar 16, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.base.MoreObjects;

public class PatientName
{
  @JsonView(View.Summary.class)
  public String family;
  @JsonView(View.Summary.class)
  public String given;
  @JsonView(View.Summary.class)
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
