/* Project: continuum
 * File: TransformationsConfig.java
 * Created: Mar 28, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.config;

import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.vumc.ContinuumApplication;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

@Configuration
public class XmlConfig
{
  @Bean(name = "cdaTransformer")
  Transformer cdaTransformer() throws Exception {
    TransformerFactory tFactory = TransformerFactory.newInstance();
    tFactory.setURIResolver((href, base) -> new StreamSource(ContinuumApplication.class.getClassLoader().getResourceAsStream(href)));
    return tFactory.newTransformer(new StreamSource(ContinuumApplication.class.getResourceAsStream("toPatientJson.xsl")));
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
