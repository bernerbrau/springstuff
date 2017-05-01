/* Project: continuum
 * File: PatientResourceControllerTest.java
 * Created: May 01, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.controllers;

import com.google.common.base.Charsets;
import org.junit.Before;
import org.junit.Test;
import org.springframework.integration.channel.QueueChannel;
import org.vumc.model.Patient;
import org.vumc.repository.PatientRepository;
import org.vumc.transformations.c32.PatientC32ConverterConfig;
import org.vumc.transformations.c32.TestConfig;

import javax.xml.transform.Transformer;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;

public class PatientResourceControllerTest
{
  private static final AtomicLong SEQ            = new AtomicLong(0);
  private static final String     SIMPLE_WRAPPER =
      "<x:Document xmlns:x=\"urn:ihe:iti:xds-b:2007\">%s</x:Document>";
  private static final String     SIMPLE_DOC =
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
  private static final String     DOC_WITH_NAME_FALLBACK =
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

  @Before
  public void setUp() throws Exception
  {
    repoBackingMap = new HashMap<>();
    channel = new QueueChannel();

    Transformer transformer = new TestConfig().patientC32DocumentTransformer();

    PatientRepository fakeRepository = mock(PatientRepository.class);
    given(fakeRepository.save(any(Patient.class)))
        .willAnswer(i ->
        {
          Patient p = i.getArgument(0);
          p.setId(SEQ.getAndIncrement());
          repoBackingMap.put(p.getId(), p);
          return p;
        });

    mController = new PatientResourceController(
       new PatientC32ConverterConfig()
           .patientC32Converter(transformer),
       fakeRepository,
       channel
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

    assertEquals("BIRD", dbPatient.getName().getFamily());
    assertEquals("BIG", dbPatient.getName().getGiven());
    assertEquals("ESQ", dbPatient.getName().getSuffix());
    assertEquals("M", dbPatient.getGender());
    assertEquals(payload, dbPatient.getRawMessage());
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

    assertEquals("BIRD", dbPatient.getName().getFamily());
    assertEquals("BIG", dbPatient.getName().getGiven());
    assertEquals("ESQ", dbPatient.getName().getSuffix());
    assertEquals("M", dbPatient.getGender());
    assertEquals(payload, dbPatient.getRawMessage());
  }

  @Test
  public void testFallbackName() throws Exception
  {
    String payload = DOC_WITH_NAME_FALLBACK;

    mController.postC32Document(payload);

    assertEquals(1, repoBackingMap.size());
    Patient dbPatient = repoBackingMap.values().iterator().next();
    Patient msgPatient = (Patient) channel.receive(1).getPayload();

    assertSame(dbPatient, msgPatient);

    assertEquals("BIG ESQ", dbPatient.getName().getName());
    assertEquals("M", dbPatient.getGender());
    assertEquals(payload, dbPatient.getRawMessage());
  }
}
