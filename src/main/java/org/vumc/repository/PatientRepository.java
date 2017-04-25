/* Project: continuum
 * File: PatientRepository.java
 * Created: Mar 03, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * Patienthis code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.vumc.model.DefinedAuthority;
import org.vumc.model.Patient;
import org.vumc.security.annotations.AllowedAuthorities;

import java.util.Collection;
import java.util.List;

@RepositoryRestResource
public interface PatientRepository extends CrudRepository<Patient, Long>
{
  @Override
  @RestResource(exported=false)
  @AllowedAuthorities(DefinedAuthority.PATIENT_SOURCE)
  <S extends Patient> S save(S entity);

  @Override
  @RestResource(exported=false)
  @AllowedAuthorities({DefinedAuthority.PROVIDER, DefinedAuthority.SYSTEM})
  Patient findOne(Long inLong);

  @Override
  @AllowedAuthorities({DefinedAuthority.PROVIDER, DefinedAuthority.SYSTEM})
  Collection<Patient> findAll();

  @Override
  @RestResource(exported=false)
  @PreAuthorize("denyAll()")
  <S extends Patient> List<S> save(Iterable<S> entities);

  @Override
  @RestResource(exported=false)
  @PreAuthorize("denyAll()")
  boolean exists(Long inLong);

  @Override
  @RestResource(exported=false)
  @PreAuthorize("denyAll()")
  Collection<Patient> findAll(Iterable<Long> ids);

  @Override
  @RestResource(exported=false)
  @PreAuthorize("denyAll()")
  long count();

  @Override
  @RestResource(exported=false)
  @PreAuthorize("denyAll()")
  void delete(Long inLong);

  @Override
  @RestResource(exported=false)
  @PreAuthorize("denyAll()")
  void delete(Patient entity);

  @Override
  @RestResource(exported=false)
  @PreAuthorize("denyAll()")
  void delete(Iterable<? extends Patient> entities);

  @Override
  @RestResource(exported=false)
  @PreAuthorize("denyAll()")
  void deleteAll();

}