/* Project: continuum
 * File: IntegrationConfig.java
 * Created: Apr 17, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.core.Pollers;
import org.springframework.integration.dsl.support.MessageProcessorMessageSource;
import org.springframework.messaging.MessageChannel;
import org.vumc.aop.RunAsSystemAuthorityMethodInteceptor;
import org.vumc.repository.PatientRepository;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class IntegrationConfig
{
  @Bean(name="newPatients")
  public MessageChannel newPatients() {
    return new PublishSubscribeChannel();
  }

  @Bean
  public IntegrationFlow getPatientPoll(
      PatientRepository inPatientRepository) {
    return
        IntegrationFlows.from(
            new MessageProcessorMessageSource(
                (msg) -> inPatientRepository.findAll()
            ),
            c -> c.poller(
                Pollers
                    .fixedDelay(5, TimeUnit.SECONDS)
                    .advice(new RunAsSystemAuthorityMethodInteceptor()))
        )
        .split(List.class, patients -> patients)
        .channel("newPatients")
        .get();
  }
}
