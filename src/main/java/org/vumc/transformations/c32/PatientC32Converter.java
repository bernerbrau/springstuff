/* Project: continuum
 * File: PatientC32Converter.java
 * Created: Mar 31, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.transformations.c32;

import com.google.common.base.Charsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vumc.model.Patient;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
public class PatientC32Converter
{
  private final PatientDocumentC32Finder finder;
  private final PatientC32Extractor extractor;
  private final DocumentBuilder docBuilder;

  @Autowired
  public PatientC32Converter(final PatientDocumentC32Finder inFinder,
                             final PatientC32Extractor inExtractor)
      throws ParserConfigurationException
  {
    finder = inFinder;
    extractor = inExtractor;
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    docBuilder = factory.newDocumentBuilder();
  }

  public Patient convert(String xmlAsString) {
    try (InputStream xmlAsStream = new ByteArrayInputStream(xmlAsString.getBytes(Charsets.UTF_8)))
    {
      return convert(xmlAsStream, xmlAsString);
    }
    catch (IOException e) {
      throw new InvalidTransformationResultException("Error parsing C32 document: " + e.getMessage());
    }
  }

  public Patient convert(InputStream xmlAsStream, String xmlAsString) {
    try
    {
      Node node = finder.findC32Document(docBuilder.parse(xmlAsStream));
      return extractor.extractPatient(node, xmlAsString);
    }
    catch (IOException | SAXException | XPathExpressionException | TransformerException e) {
      throw new InvalidTransformationResultException("Error parsing C32 document: " + e.getMessage());
    }
  }
}
