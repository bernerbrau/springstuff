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

  public static final String APPLICATION_TYPE = "application";
  public static final String HAL_JSON_SUBTYPE = "hal+json";
  public static final String HAL_JSON_TYPE = APPLICATION_TYPE + "/" + HAL_JSON_SUBTYPE;

  private Observable<Patient> patientObservable;
  private SimpMessagingTemplate                              webSocketTemplate;
  private Subscription                                       subscription;
  private PersistentEntityResourceAssembler                  assembler;
  private TypeConstrainedMappingJackson2HttpMessageConverter jacksonHttpMessageConverter;

  @Autowired
  public WebSocketPatientController(
        @Qualifier("patientObservable")
        final Observable<Patient> inPatientObservable,
        final SimpMessagingTemplate inWebSocketTemplate,
        final PersistentEntityResourceAssembler inPersistentEntityResourceAssembler,
        final TypeConstrainedMappingJackson2HttpMessageConverter jacksonHttpMessageConverter)
  {
    patientObservable = inPatientObservable;
    webSocketTemplate = inWebSocketTemplate;
    assembler = inPersistentEntityResourceAssembler;
    this.jacksonHttpMessageConverter = jacksonHttpMessageConverter;
  }

  @PostConstruct
  void subscribeToPatientFeed() {
    subscription = patientObservable.subscribe(
        patient ->
            webSocketTemplate.send(
                "/topic/patients",
                convert(patient)));
  }

  private Message<?> convert(final Patient inPatient)
  {
    ResourceSupport resource = assembler.toFullResource(inPatient);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    // TODO add link processors
    try
    {
      jacksonHttpMessageConverter
          .write(resource, new MediaType(APPLICATION_TYPE, HAL_JSON_SUBTYPE), new HttpOutputMessage()
          {
            @Override
            public OutputStream getBody() throws IOException
            {
              return output;
            }

            @Override
            public HttpHeaders getHeaders()
            {
              return new HttpHeaders();
            }
          });
      output.flush();
    } catch (IOException e) {
      throw new RuntimeException("Could not convert Patient to HAL+JSON", e);
    }

    return new GenericMessage<>(output.toByteArray(), ImmutableMap.of(MessageHeaders.CONTENT_TYPE, HAL_JSON_TYPE));
  }

  @PreDestroy
  void unsubscribe() {
    subscription.unsubscribe();
  }

}