/* Project: continuum
 * File: RawC32Repository.java
 * Created: May 04, 2017
 * Author: Josh Yarbrough - josh.yarbrough@vumc.org
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.vumc.model.RawMessage;

import java.time.ZonedDateTime;


@Repository
public interface RawC32Repository extends CrudRepository<RawMessage, Long>
{
  RawMessage findTop1ByStatusAndProcessTriesLessThanAndAccessedLessThanOrderByProcessTriesAscAccessedDesc
      (char inStatusError, int inMaxProcessTries, ZonedDateTime inMaxAccessedTime);
}