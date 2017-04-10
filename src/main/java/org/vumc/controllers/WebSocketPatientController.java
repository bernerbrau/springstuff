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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.vumc.model.Patient;
import rx.Observable;
import rx.Subscription;

import javax.annotation.PreDestroy;

@Controller
public class WebSocketPatientController
{
  private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketPatientController.class);
  private Subscription subscription;

  @Autowired
  void subscribeToPatientFeed(@Qualifier("patientObservable")
                              final Observable<Patient> patientObservable,
                              final SimpMessagingTemplate webSocketTemplate) {
    LOGGER.debug("patientObservable: {}; webSocketTemplate: {}",patientObservable,webSocketTemplate);

    subscription = patientObservable.subscribe(
        patient ->
            webSocketTemplate.convertAndSend(
                "/topic/patients", patient));
  }

  @PreDestroy
  void unsubscribe() {
    subscription.unsubscribe();
  }

}