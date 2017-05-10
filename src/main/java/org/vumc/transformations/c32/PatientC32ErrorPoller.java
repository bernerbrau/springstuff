/* Project: continuum
 * File: PatientC32ErrorPoller.java
 * Created: May 05, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.transformations.c32;

import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.core.MessageSource;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.vumc.model.RawMessage;
import org.vumc.repository.RawC32Repository;

import java.time.ZonedDateTime;

public class PatientC32ErrorPoller implements MessageSource<RawMessage>
{
  private final RawC32Repository repository;

  public PatientC32ErrorPoller(final RawC32Repository inRepository)
  {
    repository = inRepository;
  }

  @Transactional(isolation = Isolation.READ_COMMITTED)
  public Message<RawMessage> receive()
  {
    RawMessage message =
        repository
            .findTop1ByStatusAndProcessTriesLessThanAndAccessedLessThanOrderByProcessTriesAscAccessedDesc(
                RawMessage.STATUS_ERROR,
                5,
                ZonedDateTime.now().minusMinutes(1)
            );
    if (message == null)
    {
      return null;
    }
    else {
      message.incrementProcessTries();
      message.updateAccessed();
      return new GenericMessage<>(repository.save(message));
    }
  }

}
