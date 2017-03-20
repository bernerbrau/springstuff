package org.vumc;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.config.EnableEntityLinks;
import org.springframework.web.WebApplicationInitializer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

@SpringBootApplication
@EnableEntityLinks
public class ContinuumApplication
    extends SpringBootServletInitializer
    implements WebApplicationInitializer
{

  public static void main(String[] args)
  {
    new SpringApplicationBuilder(ContinuumApplication.class)
        .run(args);
  }

  @Override
  protected SpringApplicationBuilder configure(final SpringApplicationBuilder builder)
  {
    return builder.sources(ContinuumApplication.class);
  }

  @Bean(name = "cdaTransformer")
  Transformer cdaTransformer() throws Exception {
    TransformerFactory tFactory = TransformerFactory.newInstance();
    tFactory.setURIResolver((href, base) -> new StreamSource(getClass().getClassLoader().getResourceAsStream(href)));
    return tFactory.newTransformer(new StreamSource(getClass().getClassLoader().getResourceAsStream("toPatientJson.xsl")));
  }

  @Bean
  DocumentBuilder documentBuilder() throws Exception {
    return DocumentBuilderFactory.newInstance().newDocumentBuilder();
  }

  @Bean
  XPath xPath() throws Exception {
    return XPathFactory.newInstance().newXPath();
  }


}
