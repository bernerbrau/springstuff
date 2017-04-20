/* Project: continuum
 * File: IntegrationConfig.java
 * Created: Apr 17, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.MessageChannel;

@Configuration
public class IntegrationConfig
{
  @Bean(name="newPatients")
  public MessageChannel newPatients(@Qualifier("jdbcChannel") MessageChannel channel)
  {
    return channel;
  }
}
