/* Project: continuum
 * File: XmlConfig.java
 * Created: May 01, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class XmlConfig
{
  @Bean("patientC32DocumentTransformer")
  public Transformer patientC32DocumentTransformer(ApplicationContext ctx)
      throws IOException, TransformerConfigurationException
  {
    try(InputStream xslStream = ctx.getResource("classpath:/orionCCD_v2.4.xsl").getInputStream())
    {
      return
          TransformerFactory.newInstance().newTransformer(
              new StreamSource(xslStream));
    }
  }

}
