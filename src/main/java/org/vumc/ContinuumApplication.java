package org.vumc;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.config.EnableEntityLinks;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.crypto.codec.Utf8;
import org.springframework.web.WebApplicationInitializer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.nio.ByteBuffer;

@SpringBootApplication
@EnableEntityLinks
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
