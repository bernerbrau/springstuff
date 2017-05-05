/* Project: continuum
 * File: PatientC32ErrorPoller.java
 * Created: May 05, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.transformations.c32;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.vumc.model.RawMessage;
import org.vumc.repository.RawC32Repository;

import java.time.ZonedDateTime;

@Component
public class PatientC32ErrorPoller
{
  private final RawC32Repository repository;

  @Autowired
  public PatientC32ErrorPoller(final RawC32Repository inRepository)
  {
    repository = inRepository;
  }

  @Transactional
  @InboundChannelAdapter(channel = "rawC32",
                         poller = @Poller(fixedRate="60000", maxMessagesPerPoll = "2147483647"))
  public RawMessage pollRawMessage()
  {
    ZonedDateTime cutoff = ZonedDateTime.now().minusMinutes(1);
    RawMessage message =
        repository
            .findTop1ByStatusAndProcessTriesLessThanAndAccessedLessThanOrderByProcessTriesAscAccessedDesc(
                RawMessage.STATUS_ERROR, 5, cutoff
            );
    if (message == null)
    {
      return null;
    }
    else {
      message.incrementProcessTries();
      message.updateAccessed();
      return repository.save(message);
    }
  }

  @ServiceActivator(inputChannel = "c32Errors")
  public void handleC32Error(@Payload RawMessage error) {

  }

}
