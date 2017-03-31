/* Project: continuum
 * File: InvalidTransformationResultException.java
 * Created: Mar 20, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.transformations.c32;

public class InvalidTransformationResultException extends RuntimeException
{
  public InvalidTransformationResultException(final String message)
  {
    super(message);
  }
}
