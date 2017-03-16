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

public class PatientId
{
  @JsonView(View.Summary.class)
  public String value;
  @JsonView(View.Summary.class)
  public String root;
  @JsonView(View.Summary.class)
  public String aaName;

  @Override
  public String toString()
  {
    return MoreObjects.toStringHelper(this)
               .add("value", value)
               .add("root", root)
               .add("aaName", aaName)
               .toString();
  }
}
