/* Project: continuum
 * File: PatientRepository.java
 * Created: Mar 03, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.vumc.model.*;

import java.util.List;

public interface PatientRepository extends CrudRepository<Patient, Long>
{
  @Query
  List<? extends org.vumc.hypermedia.resources.PatientResource> findByIdGreaterThan(int inLatestId);
}