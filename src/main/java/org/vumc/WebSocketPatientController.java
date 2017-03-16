/* Project: continuum
 * File: WebSocketConfig.java
 * Created: Mar 07, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc;

import org.eclipse.collections.impl.factory.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import rx.Observable;
import rx.Subscription;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Map;

@Controller
public class WebSocketPatientController
{

  private PatientRepository     patientRepository;
  private Observable<Patient>   patientObservable;
  private SimpMessagingTemplate webSocketTemplate;
  private Subscription          subscription;

  @Autowired
  public WebSocketPatientController(
      @Qualifier("patientObservable") final Observable<Patient> inPatientObservable,
      final SimpMessagingTemplate inWebSocketTemplate,
      final PatientRepository inPatientRepository)
  {
    patientObservable = inPatientObservable;
    webSocketTemplate = inWebSocketTemplate;
    patientRepository = inPatientRepository;
  }



  @PostConstruct
  void subscribeToPatientFeed() {
    Map<String, Object> headers = Maps.immutable.<String,Object> of(
        "conversionHint",View.Summary.class
    ).castToMap();
    subscription = patientObservable.subscribe(
        patient ->
            webSocketTemplate.convertAndSend(
                "/topic/patients",
                patient,
                headers));
  }

  @PreDestroy
  void unsubscribe() {
    subscription.unsubscribe();
  }

}