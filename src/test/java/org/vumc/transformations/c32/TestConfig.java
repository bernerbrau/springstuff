/* Project: continuum
 * File: TestConfig.java
 * Created: May 01, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.transformations.c32;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;

public class TestConfig
{
  public Transformer patientC32DocumentTransformer()
      throws IOException, TransformerConfigurationException
  {
      return
          TransformerFactory.newInstance()
              .newTransformer(
                  new StreamSource(
                      new File("./src/main/resources/orionCCD_v2.4.xsl")
                  )
              );
  }
}
