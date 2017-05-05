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
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.vumc.security.InternalSystemAuthentication;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableIntegration
public class IntegrationConfig
{
  @Bean
  public Executor messageExecutor() {
    ConcurrentTaskExecutor executor
        = new ConcurrentTaskExecutor(
            Executors.newFixedThreadPool(2,
                r -> {
                  Thread t = new Thread(r);
                  t.setName("IntegrationConfig.messageExecutor");
                  return t;
                }
            ));
    executor.setTaskDecorator(new InternalSystemAuthentication().getTaskDecorator());
    return executor;
  }

  @Bean(name="newPatients")
  public MessageChannel newPatients(@Qualifier("jdbcChannel") MessageChannel channel)
  {
    return channel;
  }

  @Bean(name="rawC32")
  public MessageChannel rawC32(@Qualifier("messageExecutor") Executor executor) {
    return new ExecutorChannel(executor);
  }

}
