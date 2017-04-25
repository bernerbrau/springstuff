/* Project: continuum
 * File: WebSocketConfig.java
 * Created: Mar 07, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.vumc.model.Patient;

@Controller
public class WebSocketPatientController
{
  private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketPatientController.class);
  private final SimpMessagingTemplate webSocketTemplate;

  @Autowired
  public WebSocketPatientController(final SimpMessagingTemplate inWebSocketTemplate)
  {
    webSocketTemplate = inWebSocketTemplate;
  }

  @ServiceActivator(inputChannel = "newPatients")
  public void broadcastNewPatient(Message<Patient> patient) {
    webSocketTemplate.convertAndSend("/topic/patients", patient.getPayload().getId());
  }

}