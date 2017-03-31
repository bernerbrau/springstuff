/* Project: continuum
 * File: WeblogicAnnotationConfigEmbeddedWebApplicationConfiguration.java
 * Created: Mar 31, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.weblogic;

import com.google.common.collect.Lists;
import org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext;
import org.springframework.boot.web.servlet.ServletContextInitializer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

// HACK to fix this problem: https://github.com/spring-projects/spring-boot/issues/2643
public class WeblogicAnnotationConfigEmbeddedWebApplicationContext
    extends AnnotationConfigEmbeddedWebApplicationContext
{
  @Override
  protected Collection<ServletContextInitializer> getServletContextInitializerBeans()
  {
    Collection<ServletContextInitializer> initializers = super.getServletContextInitializerBeans();

    List<ServletContextInitializer> filters = initializers.stream()
                                                 .filter(i -> i.getClass().getName().contains("Filter"))
                                                 .collect(Collectors.toList());
    List<ServletContextInitializer> noFilters = initializers.stream()
                                                     .filter(i -> !i.getClass().getName().contains("Filter"))
                                                     .collect(Collectors.toList());

    Collection<ServletContextInitializer> reversedInitializers = new ArrayList<>(initializers.size());
    reversedInitializers.addAll(noFilters);
    reversedInitializers.addAll(Lists.reverse(filters));

    return reversedInitializers;
  }
}
