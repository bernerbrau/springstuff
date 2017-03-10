/* Project: continuum
 * File: PatientRepository.java
 * Created: Mar 03, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc;

import java.util.List;

public interface PatientRepository
{

  List<? extends Patient> findAll();

  Patient find(int inId);

  int getNextId();
}