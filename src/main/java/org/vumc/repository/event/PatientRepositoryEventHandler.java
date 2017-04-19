/* Project: continuum
 * File: PatientRepositoryEventHandler.java
 * Created: Apr 18, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.repository.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;
import org.vumc.model.Patient;

@Component
@RepositoryEventHandler
public class PatientRepositoryEventHandler
{
  private final MessageChannel newPatients;

  @Autowired
  public PatientRepositoryEventHandler(
      @Qualifier("newPatients")
      final MessageChannel inNewPatients)
  {
    newPatients = inNewPatients;
  }

  @HandleAfterSave
  public void handlePatientSave(Patient p) {
    newPatients.send(new GenericMessage<>(p));
  }

}
