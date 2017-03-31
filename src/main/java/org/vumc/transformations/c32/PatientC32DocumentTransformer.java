/* Project: continuum
 * File: PatientC32Extractor.java
 * Created: Mar 31, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.transformations.c32;

import com.google.common.base.Charsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
class PatientC32DocumentTransformer
{
  private final Transformer patientC32DocumentTransformer;

  @Autowired
  public PatientC32DocumentTransformer(ApplicationContext ctx) throws Exception
  {
    try(InputStream xslStream = ctx.getResource("classpath:/orionCCD_v2.4.xsl").getInputStream())
    {
      patientC32DocumentTransformer =
          TransformerFactory.newInstance().newTransformer(
            new StreamSource(xslStream));
    }
  }

  String c32DocumentToHTML(Node xml) throws TransformerException, IOException
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    patientC32DocumentTransformer.transform(
        new DOMSource(xml),
        new StreamResult(baos));
    baos.flush();
    return new String(baos.toByteArray(), Charsets.UTF_8);
  }

}
