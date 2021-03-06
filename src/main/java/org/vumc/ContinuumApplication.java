package org.vumc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.hateoas.config.EnableEntityLinks;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.WebApplicationContext;
import org.vumc.weblogic.WeblogicAnnotationConfigEmbeddedWebApplicationContext;

@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling
@EnableAspectJAutoProxy
@EnableJpaRepositories
@EnableEntityLinks
@EnableHypermediaSupport(type = HypermediaType.HAL)
@PropertySource(value = "classpath:continuum.system.properties", ignoreResourceNotFound = true)
public class ContinuumApplication
    extends SpringBootServletInitializer
    implements WebApplicationInitializer
{

  public static void main(String[] args) throws Exception
  {
    if (System.getProperty("spring.profiles.active") == null)
    {
      System.setProperty("spring.profiles.active", "local");
    }
    new SpringApplicationBuilder(ContinuumApplication.class)
        .run(args);
  }

  protected WebApplicationContext run(SpringApplication application) {
    // HACK to fix this problem: https://github.com/spring-projects/spring-boot/issues/2643
    application.setApplicationContextClass(WeblogicAnnotationConfigEmbeddedWebApplicationContext.class);
    return super.run(application);
  }

  @Override
  protected SpringApplicationBuilder configure(final SpringApplicationBuilder builder)
  {
    return builder.sources(ContinuumApplication.class).profiles("war");
  }

}
