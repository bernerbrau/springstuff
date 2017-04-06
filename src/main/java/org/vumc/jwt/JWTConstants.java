/* Project: continuum
 * File: JWTConstants.java
 * Created: Apr 05, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.jwt;

import java.util.regex.Pattern;

class JWTConstants
{
  static final Pattern bearerPattern = Pattern.compile("^Bearer\\s+(.+)$");
}
