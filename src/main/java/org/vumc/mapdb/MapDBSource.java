/* Project: continuum
 * File: MapDBSource.java
 * Created: Mar 10, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.mapdb;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.mapdb.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.vumc.Patient;

import javax.annotation.PreDestroy;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

class JsonSerializer<T> implements Serializer<T>
{
  private final Class<T> objectClass;
  private ObjectMapper mapper = new ObjectMapper();

  JsonSerializer(Class<T> objectClass) {
    this.objectClass = objectClass;
  }

  @Override
  public void serialize(@NotNull final DataOutput2 out, @NotNull final T value)
      throws IOException
  {
    mapper.writeValue((DataOutput) out, value);
  }

  @Override
  public T deserialize(@NotNull final DataInput2 input, final int available)
      throws IOException
  {
    return mapper.readValue(input, objectClass);
  }

}

@Configuration
public class MapDBSource
{

  private DB db = DBMaker.fileDB("patients.db")
                      .checksumHeaderBypass()
                      .transactionEnable()
                      .make();

  @Bean
  public DB getDB()
  {
    return db;
  }

  @Bean(name = "patientPersistentList")
  public List<Patient> patientBackingList(DB db)
  {
    return db
      .indexTreeList("patients", new JsonSerializer<>(Patient.class))
      .createOrOpen();
  }

  @PreDestroy
  void closeDatabase()
  {
    db.close();
  }

}
