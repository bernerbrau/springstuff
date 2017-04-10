/* Project: continuum
 * File: Validations.java
 * Created: Apr 07, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.fjutil;

import fj.data.Validation;

public final class Validations
{
  private Validations() {}

  public static <E extends Throwable, T> T getOrThrow(final Validation<E, T> val) throws E
  {
    if (val.isSuccess()) {
      return val.success();
    } else {
      throw val.fail();
    }
  }
}
