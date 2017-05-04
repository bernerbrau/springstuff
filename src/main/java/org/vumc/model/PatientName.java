/* Project: continuum
 * File: PatientId.java
 * Created: Mar 16, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.model;

import com.google.common.base.MoreObjects;

import javax.persistence.Column;

public class PatientName
{
  @Column(length=30)
  private String family;
  @Column(length=50)
  private String given;
  @Column(length=10)
  private String suffix;
  @Column(length=100)
  private String name;

  @Override
  public String toString()
  {
    return MoreObjects.toStringHelper(this)
               .add("family", family)
               .add("given", given)
               .add("suffix", suffix)
               .add("name", name)
               .toString();
  }

  public String getFamily()
  {
    return family;
  }

  public void setFamily(final String inFamily)
  {
    family = inFamily;
  }

  public String getGiven()
  {
    return given;
  }

  public void setGiven(final String inGiven)
  {
    given = inGiven;
  }

  public String getSuffix()
  {
    return suffix;
  }

  public void setSuffix(final String inSuffix)
  {
    suffix = inSuffix;
  }

  public String getName()
  {
    return name;
  }

  public void setName(final String inName)
  {
    name = inName;
  }

}
