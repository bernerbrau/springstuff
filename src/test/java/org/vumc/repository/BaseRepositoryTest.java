/* Project: spring
 * File: BaseRepositoryTest.java
 * Created: May 10, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.repository;

import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.data.keyvalue.core.KeyValueTemplate;
import org.springframework.data.keyvalue.core.mapping.KeyValuePersistentEntity;
import org.springframework.data.keyvalue.core.mapping.KeyValuePersistentProperty;
import org.springframework.data.keyvalue.core.mapping.context.KeyValueMappingContext;
import org.springframework.data.map.MapKeyValueAdapter;
import org.springframework.data.map.repository.config.EnableMapRepositories;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.vumc.config.MethodSecurityConfig;

import javax.persistence.Id;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RunWith(SpringRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public abstract class BaseRepositoryTest
{
  @Configuration
  @EnableMapRepositories(basePackages = "org.vumc.repository")
  @Import(MethodSecurityConfig.class)
  public static class BaseRepositoryTestConfig {

    private Map<Serializable, Map<Serializable, Object>> backingMap = new ConcurrentHashMap<>();

    @Bean
    @Primary
    public KeyValueTemplate mapKeyValueTemplate(KeyValueMappingContext context) {
      return new KeyValueTemplate(new MapKeyValueAdapter(backingMap), context);
    }

    @Bean
    public KeyValueMappingContext keyValueMappingContext() {
      return new KeyValueMappingContext() {
        @Override
        protected KeyValuePersistentProperty createPersistentProperty(
            final Field field,
            final PropertyDescriptor descriptor,
            final KeyValuePersistentEntity<?> owner,
            final SimpleTypeHolder simpleTypeHolder)
        {
          return new KeyValuePersistentProperty(field, descriptor, owner, simpleTypeHolder){
            @Override
            public boolean isIdProperty()
            {
              return isAnnotationPresent(Id.class);
            }
          };
        }
      };
    }

  }



}
