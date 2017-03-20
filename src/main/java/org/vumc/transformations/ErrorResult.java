/* Project: continuum
 * File: ErrorResult.java
 * Created: Mar 20, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.transformations;

import com.google.common.base.MoreObjects;

public class ErrorResult
{
  public String error;

  @Override
  public String toString()
  {
    return MoreObjects.toStringHelper(this)
               .add("error", error)
               .toString();
  }
}
