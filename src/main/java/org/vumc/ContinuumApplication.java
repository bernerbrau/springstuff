package org.vumc;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.hateoas.config.EnableEntityLinks;
import org.springframework.web.WebApplicationInitializer;

@SpringBootApplication
@EnableEntityLinks
@EnableJpaAuditing
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

  @Override
  protected SpringApplicationBuilder configure(final SpringApplicationBuilder builder)
  {
    return builder.sources(ContinuumApplication.class).profiles("war");
  }

}
