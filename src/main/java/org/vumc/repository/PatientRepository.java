/* Project: continuum
 * File: PatientRepository.java
 * Created: Mar 03, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * Patienthis code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.repository;

import org.springframework.data.repository.RepositoryDefinition;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.vumc.model.DefinedAuthority;
import org.vumc.model.Patient;
import org.vumc.security.annotations.AllowedAuthorities;

import java.time.ZonedDateTime;
import java.util.Collection;

@RepositoryRestResource
@RepositoryDefinition(domainClass = Patient.class, idClass = long.class)
public interface PatientRepository
{
  @AllowedAuthorities(DefinedAuthority.PROVIDER)
  Patient findOne(long inId);

  @AllowedAuthorities(DefinedAuthority.PROVIDER)
  Collection<Patient> findAll();

  @AllowedAuthorities(DefinedAuthority.PROVIDER)
  Collection<Patient> findByCreatedAfter(@Param("created") @DateTimeFormat(iso = ISO.DATE_TIME) ZonedDateTime created);

  @RestResource(exported=false)
  @AllowedAuthorities(DefinedAuthority.SYSTEM)
  <S extends Patient> S save(S inPatient);

}