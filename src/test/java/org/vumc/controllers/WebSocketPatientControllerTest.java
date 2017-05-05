/* Project: continuum
 * File: WebSocketPatientControllerTest.java
 * Created: May 05, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.controllers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.AbstractPollableChannel;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.vumc.config.IntegrationConfig;
import org.vumc.model.Patient;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
public class WebSocketPatientControllerTest
{
  @Configuration
  public static class TestIntConfig extends IntegrationConfig {}

  @Configuration
  public static class TestConfig {
    @Bean
    public QueueChannel websocketChannel()
    {
      return new QueueChannel();
    }
    @Bean
    public DirectChannel jdbcChannel()
    {
      return new DirectChannel();
    }
    @Bean
    public SimpMessagingTemplate simpMessagingTemplate(@Qualifier("websocketChannel") MessageChannel websocketChannel)
    {
      return new SimpMessagingTemplate(websocketChannel);
    }
    @Bean
    public WebSocketPatientController controller(SimpMessagingTemplate messagingTemplate) {
      return new WebSocketPatientController(messagingTemplate);
    }
  }

  @Autowired
  @Qualifier("newPatients")
  private MessageChannel newPatients;

  @Autowired
  @Qualifier("websocketChannel")
  private AbstractPollableChannel websocketChannel;

  @Test
  public void messageOnNewPatientsIsRoutedToWebsocketPatientTopicAsPatientId() {
    final long patientId = 42;
    Patient patient = new Patient();
    patient.setId(patientId);
    newPatients.send(new GenericMessage<>(patient));

    Message<?> message = websocketChannel.receive(0);
    assertEquals(Long.toString(patientId), message.getPayload().toString());

    SimpMessageHeaderAccessor simpAccessor =
        MessageHeaderAccessor.getAccessor(message, SimpMessageHeaderAccessor.class);
    assertEquals("/topic/patients", simpAccessor.getDestination());
  }
}