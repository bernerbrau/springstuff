/* Project: continuum
 * File: PatientResourceControllerTest.java
 * Created: May 01, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.controllers;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.vumc.model.Patient;
import org.vumc.model.RawMessage;
import org.vumc.repository.PatientRepository;
import org.vumc.repository.RawC32Repository;
import org.vumc.transformations.c32.PatientC32Converter;
import org.vumc.transformations.c32.PatientC32ConverterConfig;

import javax.xml.transform.Transformer;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@RunWith(SpringRunner.class)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class PatientResourceControllerTest
{
  private static final String SIMPLE_WRAPPER =
      "<x:Document xmlns:x=\"urn:ihe:iti:xds-b:2007\">%s</x:Document>";
  private static final String SIMPLE_DOC              =
      "<ClinicalDocument xmlns=\"urn:hl7-org:v3\">\n" +
      "    <recordTarget>\n" +
      "        <patientRole>\n" +
      "            <id assigningAuthorityName=\"Vanderbilt University Medical Center\" extension=\"012345678\" />\n" +
      "            <patient>\n" +
      "                <name>\n" +
      "                    <given>BIG</given>\n" +
      "                    <family>BIRD</family>\n" +
      "                    <suffix>ESQ</suffix>\n" +
      "                </name>\n" +
      "                <administrativeGenderCode displayName=\"M\" />\n" +
      "                <birthTime value=\"19610425\"/>\n" +
      "            </patient>\n" +
      "        </patientRole>\n" +
      "    </recordTarget>\n" +
      "</ClinicalDocument>\n";
  private static final String DOC_WITH_NO_FAMILY_NAME =
      "<ClinicalDocument xmlns=\"urn:hl7-org:v3\">\n" +
      "    <recordTarget>\n" +
      "        <patientRole>\n" +
      "            <id assigningAuthorityName=\"Vanderbilt University Medical Center\" extension=\"012345678\" />\n" +
      "            <patient>\n" +
      "                <name>\n" +
      "                    <given>BIG</given>\n" +
      "                    <suffix>ESQ</suffix>\n" +
      "                </name>\n" +
      "                <administrativeGenderCode displayName=\"M\" />\n" +
      "                <birthTime value=\"19610425\"/>\n" +
      "            </patient>\n" +
      "        </patientRole>\n" +
      "    </recordTarget>\n" +
      "</ClinicalDocument>\n";

  @Autowired
  private PatientResourceController mController;
  @Autowired
  @Qualifier("newPatients")
  private QueueChannel              channel;

  @Autowired
  @Qualifier("repoBackingMap")
  private Map<Long, Patient>        repoBackingMap;
  @Autowired
  @Qualifier("repoC32BackingMap")
  private Map<Long, RawMessage>  repoC32BackingMap;

  @Configuration
  @EnableIntegration
  @Import(org.vumc.config.TestConfig.class)
  public static class TestConfig {
    @Bean
    public DirectChannel rawC32() {
      return new DirectChannel();
    }

    @Bean
    public QueueChannel newPatients() {
      return new QueueChannel();
    }

    @Bean
    public Map<Long, Patient> repoBackingMap() {
      return new HashMap<>();
    }

    @Bean
    public Map<Long, RawMessage> repoC32BackingMap() {
      return new HashMap<>();
    }

    @Bean
    public PatientResourceController patientResourceController(
        final @Qualifier("patientC32DocumentTransformer")
            Transformer patientC32DocumentTransformer,
        final @Qualifier("rawC32") MessageChannel channel,
        final @Qualifier("repoBackingMap") Map<Long, Patient> repoBackingMap,
        final @Qualifier("repoC32BackingMap") Map<Long, RawMessage> repoC32BackingMap) throws Exception {
      final PatientC32Converter converter = new PatientC32ConverterConfig().patientC32Converter(patientC32DocumentTransformer);

      AtomicLong sequence = new AtomicLong(0);
      AtomicLong sequenceC32 = new AtomicLong(0);

      RawC32Repository fakeC32Repository = mock(RawC32Repository.class);
      given(fakeC32Repository.save(any(RawMessage.class)))
          .willAnswer(i ->
              {
                RawMessage m = i.getArgument(0);
                if (!repoC32BackingMap.containsKey(m.getId()))
                {
                  m.setId(sequenceC32.getAndIncrement());
                }
                repoC32BackingMap.put(m.getId(),m);
                return m;
              }
          );
      given(fakeC32Repository.findOne(anyLong()))
          .willAnswer(i-> repoC32BackingMap.get(i.getArgument(0)));

      PatientRepository fakeRepository = mock(PatientRepository.class);
      given(fakeRepository.save(any(Patient.class)))
          .willAnswer(i ->
          {
            Patient p = i.getArgument(0);
            if (!repoBackingMap.containsKey(p.getId()))
            {
              p.setId(sequence.getAndIncrement());
            }
            repoBackingMap.put(p.getId(), p);
            return p;
          });
      given(fakeRepository.findOne(anyLong()))
          .willAnswer(i -> repoBackingMap.get(i.getArgument(0)));

      return new PatientResourceController(
         converter,
         fakeRepository,
         fakeC32Repository,
         channel
      );

    }
  }

  @Test
  public void testBase64InSOAPWrapper() throws Exception
  {
    String payload = String.format(
        SIMPLE_WRAPPER,
        new String(
            Base64.getMimeEncoder().encode(
                SIMPLE_DOC.getBytes(Charsets.UTF_8)
            ),
            Charsets.UTF_8
        )
    );

    mController.postC32Document(payload);

    assertEquals(1, repoBackingMap.size());
    Patient dbPatient = repoBackingMap.values().iterator().next();
    Patient msgPatient = (Patient) channel.receive(1).getPayload();

    assertSame(dbPatient, msgPatient);

    assertEquals("012345678", dbPatient.getPatientId());
    assertEquals("Vanderbilt University Medical Center", dbPatient.getIdAssigningAuthority());
    assertEquals("BIRD", dbPatient.getName().getFamily());
    assertEquals("BIG", dbPatient.getName().getGiven());
    assertEquals("ESQ", dbPatient.getName().getSuffix());
    assertEquals("M", dbPatient.getGender());
    assertEquals(payload, CharStreams.toString(dbPatient.getRawMessage().getCharacterStream()));
  }

  @Test
  public void testRawC32SavedToRepository() throws Exception{
    String payload = SIMPLE_DOC;

    mController.postC32Document(payload);

    assertEquals(1,repoC32BackingMap.size());
    RawMessage dbMsg = repoC32BackingMap.values().iterator().next();

    assertNotNull(dbMsg);

    String c32String = dbMsg.getRawMessage();
    assertEquals(c32String,payload);
  }


  @Test
  public void testUnwrappedXml() throws Exception
  {
    String payload = SIMPLE_DOC;

    mController.postC32Document(payload);

    assertEquals(1, repoBackingMap.size());
    Patient dbPatient = repoBackingMap.values().iterator().next();
    Patient msgPatient = (Patient) channel.receive(1).getPayload();

    assertSame(dbPatient, msgPatient);

    assertEquals("012345678", dbPatient.getPatientId());
    assertEquals("Vanderbilt University Medical Center", dbPatient.getIdAssigningAuthority());
    assertEquals("BIRD", dbPatient.getName().getFamily());
    assertEquals("BIG", dbPatient.getName().getGiven());
    assertEquals("ESQ", dbPatient.getName().getSuffix());
    assertEquals("M", dbPatient.getGender());
    assertEquals(payload, CharStreams.toString(dbPatient.getRawMessage().getCharacterStream()));
  }

  @Test
  public void testFallbackName() throws Exception
  {
    String payload = DOC_WITH_NO_FAMILY_NAME;

    mController.postC32Document(payload);

    assertEquals(1, repoBackingMap.size());
    Patient dbPatient = repoBackingMap.values().iterator().next();
    Patient msgPatient = (Patient) channel.receive(1).getPayload();

    assertSame(dbPatient, msgPatient);

    assertEquals("BIG ESQ", dbPatient.getName().getName());
    assertEquals("M", dbPatient.getGender());
    assertEquals(payload, CharStreams.toString(dbPatient.getRawMessage().getCharacterStream()));
  }


  @Test
  public void testBody() throws Exception
  {
    mController.postC32Document(SIMPLE_DOC);

    assertEquals(1, repoBackingMap.size());
    Patient dbPatient = repoBackingMap.values().iterator().next();

    String dbBody = dbPatient == null ? null :
                    dbPatient.getBody() == null ? null :
                    CharStreams.toString(dbPatient.getBody().getCharacterStream());

    assertNotNull(dbBody);

    String controllerBody = mController.getHtml(dbPatient.getId()).getBody();

    assertEquals(dbBody, controllerBody);
  }

  @Test
  public void patientWithNoBodyReturns404() throws Exception
  {
    mController.postC32Document(SIMPLE_DOC);

    assertEquals(1, repoBackingMap.size());
    Patient dbPatient = repoBackingMap.values().iterator().next();
    dbPatient.setBody(null);

    assertEquals(404, mController.getHtml(dbPatient.getId()).getStatusCodeValue());
  }

  @Test
  public void missingPatientReturns404() throws Exception
  {
    assertEquals(404, mController.getHtml(6).getStatusCodeValue());
  }
}
