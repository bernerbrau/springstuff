/* Project: continuum
 * File: DataRestConfig.java
 * Created: Mar 29, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.support.SelfLinkProvider;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.data.rest.webmvc.mapping.Associations;
import org.springframework.data.rest.webmvc.support.PersistentEntityProjector;
import org.springframework.hateoas.mvc.ResourceProcessorInvoker;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.vumc.model.Patient;


@Configuration
public class DataRestConfig
{
  @Bean
  public PersistentEntityResourceAssembler linkAssemblerBean(
      final RepositoryRestConfiguration config,
      final Associations links,
      final PersistentEntities entities,
      final SelfLinkProvider linkProvider,
      final ApplicationContext applicationContext)
  {
    final SpelAwareProxyProjectionFactory projectionFactory = new SpelAwareProxyProjectionFactory();
    projectionFactory.setBeanFactory(applicationContext);
    projectionFactory.setBeanClassLoader(applicationContext.getClassLoader());

    final PersistentEntityProjector projector =
        new PersistentEntityProjector(config.getProjectionConfiguration(), projectionFactory,
                                         null, links.getMappings());
    return new PersistentEntityResourceAssembler(entities, projector, links, linkProvider);
  }

  @Bean
  @ConditionalOnProperty(name = "enableCORS", havingValue = "true")
  public FilterRegistrationBean corsFilter(Environment environment) {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(true);
    config.addExposedHeader("X-Auth-Token");
    config.addAllowedOrigin("*");
    config.addAllowedHeader("*");
    config.addAllowedMethod("OPTIONS");
    config.addAllowedMethod("HEAD");
    config.addAllowedMethod("GET");
    config.addAllowedMethod("PUT");
    config.addAllowedMethod("POST");
    config.addAllowedMethod("DELETE");
    config.addAllowedMethod("PATCH");
    source.registerCorsConfiguration("/**", config);
    final FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
    bean.setOrder(0);
    return bean;
  }

  @Bean
  public RepositoryRestConfigurer repositoryConfig() {
    return new RepositoryRestConfigurerAdapter()
    {
      @Override
      public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config)
      {
        config.exposeIdsFor(Patient.class);
        config.getMetadataConfiguration().setAlpsEnabled(false);
      }
    };
  }

}
