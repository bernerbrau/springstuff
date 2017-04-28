/* Project: continuum
 * File: PatientRepository.java
 * Created: Mar 03, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * Patienthis code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.repository;

import org.springframework.data.repository.RepositoryDefinition;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.vumc.model.DefinedAuthority;
import org.vumc.model.Patient;
import org.vumc.security.annotations.AllowedAuthorities;

import java.util.Collection;

@RepositoryRestResource
@RepositoryDefinition(domainClass = Patient.class, idClass = Long.class)
public interface PatientRepository
{
  @AllowedAuthorities(DefinedAuthority.PROVIDER)
  Patient findOne(Long inId);

  @AllowedAuthorities(DefinedAuthority.PROVIDER)
  Collection<Patient> findAll();

  @RestResource(exported=false)
  @AllowedAuthorities(DefinedAuthority.PATIENT_SOURCE)
  <S extends Patient> S save(S inPatient);

}