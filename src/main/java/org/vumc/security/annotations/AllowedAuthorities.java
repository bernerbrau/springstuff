/* Project: continuum
 * File: PreAuthorizeDefinedAuthorities.java
 * Created: Apr 24, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.security.annotations;

import org.vumc.model.DefinedAuthority;

import java.lang.annotation.*;

@Target( {ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface AllowedAuthorities
{
  DefinedAuthority[] value();
}
