/* Project: continuum
 * File: PatientRepository.java
 * Created: Mar 03, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.vumc.model.Patient;

@RepositoryRestResource
public interface PatientRepository extends CrudRepository<Patient, Long>
{
}