/* Project: continuum
 * File: NullAuditor.java
 * Created: Apr 25, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.repository.auditing;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

@Component("noopAuditor")
public class NoopAuditor implements AuditorAware<Object>
{
  @Override
  public Object getCurrentAuditor()
  {
    return new Object();
  }
}