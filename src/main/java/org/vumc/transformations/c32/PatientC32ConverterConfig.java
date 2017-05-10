/* Project: continuum
 * File: PatientC32ConverterConfig.java
 * Created: May 01, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.transformations.c32;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.vumc.repository.RawC32Repository;
import org.vumc.transformations.xml.XPathSource;

import javax.xml.transform.Transformer;

@Configuration
public class PatientC32ConverterConfig
{
  @Bean
  public PatientC32Converter patientC32Converter(
      @Qualifier("patientC32DocumentTransformer")
          Transformer patientC32DocumentTransformer) throws Exception {
    XPathSource xPath = new CCDXslNamespaceXPathSource();
    return new PatientC32Converter(
       new PatientDocumentC32Finder(xPath),
       new PatientC32Extractor(xPath,
           new PatientC32DocumentTransformer(
               patientC32DocumentTransformer
           )
       )
    );
  }

  @Bean
  @InboundChannelAdapter(channel = "rawC32",
                         poller=@Poller(fixedDelay="60000", maxMessagesPerPoll="-1"))
  public PatientC32ErrorPoller patientC32ErrorPoller(final RawC32Repository inRepository)
  {
    return new PatientC32ErrorPoller(inRepository);
  }

}
