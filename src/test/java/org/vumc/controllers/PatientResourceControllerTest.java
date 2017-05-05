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
import org.junit.Before;
import org.junit.Test;
import org.springframework.integration.channel.QueueChannel;
import org.vumc.config.TestConfig;
import org.vumc.model.Patient;
import org.vumc.model.RawMessage;
import org.vumc.repository.PatientRepository;
import org.vumc.repository.RawC32Repository;
import org.vumc.transformations.c32.PatientC32ConverterConfig;
import org.vumc.transformations.c32.RawC32RecordCreator;

import javax.xml.transform.Transformer;
import java.io.Reader;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;

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

  private PatientResourceController mController;
  private Map<Long, Patient>        repoBackingMap;
  private QueueChannel              channel;
  private Map<Long, RawMessage>  repoC32BackingMap;

  @Before
  public void setUp() throws Exception
  {
    AtomicLong sequence = new AtomicLong(0);
    AtomicLong sequenceC32 = new AtomicLong(0);

    repoBackingMap = new HashMap<>();
    channel = new QueueChannel();

    repoC32BackingMap = new HashMap<>();

    RawC32Repository fakeC32Repository = mock(RawC32Repository.class);
    given(fakeC32Repository.save(any(RawMessage.class)))
            .willAnswer(i ->
              {
                RawMessage m = i.getArgument(0);
                m.setId(sequenceC32.getAndIncrement());
                repoC32BackingMap.put(m.getId(),m);
                return m;
              }
            );
    given(fakeC32Repository.findOne(anyLong()))
            .willAnswer(i-> repoC32BackingMap.get(i.getArgument(0)));

    Transformer transformer = new TestConfig().patientC32DocumentTransformer();

    PatientRepository fakeRepository = mock(PatientRepository.class);
    given(fakeRepository.save(any(Patient.class)))
        .willAnswer(i ->
        {
          Patient p = i.getArgument(0);
          p.setId(sequence.getAndIncrement());
          repoBackingMap.put(p.getId(), p);
          return p;
        });
    given(fakeRepository.findOne(anyLong()))
        .willAnswer(i -> repoBackingMap.get(i.getArgument(0)));

    mController = new PatientResourceController(
       new PatientC32ConverterConfig()
           .patientC32Converter(transformer),
       fakeRepository,
       channel,
       new RawC32RecordCreator(),
       fakeC32Repository
    );
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

    String c32String = "";
    if (dbMsg != null)
    {
      Reader c32 = dbMsg.getRawMessage().getCharacterStream();
      if (c32 != null)
      {
        c32String = CharStreams.toString(c32);
      }
    }
    assertSame(c32String,payload);
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
