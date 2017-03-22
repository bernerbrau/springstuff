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
import org.springframework.stereotype.Repository;
import org.vumc.hypermedia.resources.PatientResource;
import org.vumc.model.*;

import java.util.List;

@Repository
public interface PatientRepository extends CrudRepository<Patient, Long>
{
  @Query
  List<? extends PatientResource> findByIdGreaterThan(int inLatestId);
}