/* Project: spring
 * File: PatientRepositoryTest.java
 * Created: May 10, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.repository;

import com.google.common.io.CharStreams;
import org.hibernate.engine.jdbc.NonContextualLobCreator;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.vumc.model.DefinedAuthority;
import org.vumc.model.Patient;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;
import static org.vumc.security.SecurityTestUtil.withAuthority;

public class PatientRepositoryTest extends BaseRepositoryTest
{
  @Autowired
  private PatientRepository repo;

  @Test
  public void testSaveAndLoadPatient() throws Exception {
    Patient p = new Patient();
    p.setCreated(ZonedDateTime.now());
    p.setBody(NonContextualLobCreator.INSTANCE.createClob("Hello"));
    p.setDob(LocalDate.of(1981,7,27));
    p.setGender("M");
    p.setPatientId("123412341234");
    p.setIdAssigningAuthority("Vanderbilt");
    p.getName().setFamily("Berner");
    p.getName().setGiven("Derek");

    Patient savedPatient = withAuthority(DefinedAuthority.SYSTEM, () -> repo.save(p));
    Patient loadedPatient = withAuthority(DefinedAuthority.PROVIDER, () -> repo.findOne(p.getId()));

    assertEquals(savedPatient.getId(),loadedPatient.getId());
    assertEquals(p.getCreated(),loadedPatient.getCreated());
    assertEquals(
        CharStreams.toString(p.getBody().getCharacterStream()),
        CharStreams.toString(loadedPatient.getBody().getCharacterStream()));
    assertEquals(p.getDob(),loadedPatient.getDob());
    assertEquals(p.getGender(),loadedPatient.getGender());
    assertEquals(p.getPatientId(),loadedPatient.getPatientId());
    assertEquals(p.getIdAssigningAuthority(),loadedPatient.getIdAssigningAuthority());
    assertEquals(p.getName().getFamily(),loadedPatient.getName().getFamily());
    assertEquals(p.getName().getGiven(),loadedPatient.getName().getGiven());
  }
}
