/* Project: continuum
 * File: WebSocketConfig.java
 * Created: Mar 07, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.controllers;

import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.mvc.TypeConstrainedMappingJackson2HttpMessageConverter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.TextMessage;
import org.vumc.model.Patient;
import rx.Observable;
import rx.Subscription;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;

@Controller
public class WebSocketPatientController
{

  private Observable<Patient> patientObservable;
  private SimpMessagingTemplate                              webSocketTemplate;
  private Subscription                                       subscription;

  @Autowired
  public WebSocketPatientController(
        @Qualifier("patientObservable")
        final Observable<Patient> inPatientObservable,
        final SimpMessagingTemplate inWebSocketTemplate)
  {
    patientObservable = inPatientObservable;
    webSocketTemplate = inWebSocketTemplate;
  }

  @PostConstruct
  void subscribeToPatientFeed() {
    subscription = patientObservable.subscribe(
        patient ->
            webSocketTemplate.convertAndSend(
                "/topic/patients",
                patient));
  }

  @PreDestroy
  void unsubscribe() {
    subscription.unsubscribe();
  }

}