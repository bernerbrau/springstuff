/* Project: continuum
 * File: PatientC32Extractor.java
 * Created: Mar 31, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.transformations.c32;

import com.google.common.base.Charsets;
import org.w3c.dom.Node;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

class PatientC32DocumentTransformer
{
  private final Transformer patientC32DocumentTransformer;

  PatientC32DocumentTransformer(Transformer patientC32DocumentTransformer) throws Exception
  {
    this.patientC32DocumentTransformer = patientC32DocumentTransformer;
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
