/* Project: continuum
 * File: MapDBTransactionManager.java
 * Created: Mar 10, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.mapdb;

import org.mapdb.DB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.SimpleTransactionStatus;

@Component
@EnableTransactionManagement
public class MapDBTransactionManager implements PlatformTransactionManager
{

  private DB db;

  @Autowired
  public MapDBTransactionManager(DB db) {
    this.db = db;
  }

  @Override
  public TransactionStatus getTransaction(final TransactionDefinition definition)
      throws TransactionException
  {
    return new SimpleTransactionStatus(true);
  }

  @Override
  public void commit(final TransactionStatus status) throws TransactionException
  {
    db.commit();
  }

  @Override
  public void rollback(final TransactionStatus status) throws TransactionException
  {
    db.rollback();
  }
}
