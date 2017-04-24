/* Project: continuum
 * File: MethodSecurityConfig.java
 * Created: Apr 24, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.config;

import org.springframework.security.access.method.MethodSecurityMetadataSource;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.vumc.security.annotations.DefinedAuthorityAnnotationSecurityMetadataSource;

@EnableGlobalMethodSecurity(
    prePostEnabled = true,
    proxyTargetClass = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration
{
  @Override
  protected MethodSecurityMetadataSource customMethodSecurityMetadataSource()
  {
    return new DefinedAuthorityAnnotationSecurityMetadataSource();
  }
}
