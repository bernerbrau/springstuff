/* Project: continuum
 * File: PatientDocumentC32Finder.java
 * Created: Mar 31, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.transformations.c32;

import com.google.common.base.Charsets;
import org.springframework.security.crypto.codec.Base64;
import org.vumc.transformations.xml.XPathSource;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

class PatientDocumentC32Finder
{
  private final DocumentBuilder docBuilder;
  private final XPathSource     xPath;

  PatientDocumentC32Finder(final XPathSource xPath)
      throws ParserConfigurationException
  {
    this.xPath = xPath;
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    this.docBuilder = factory.newDocumentBuilder();
  }

  Node findC32Document(Node root) throws XPathExpressionException, IOException, SAXException
  {
    Node c32doc = (Node) xPath.xpr("//n1:ClinicalDocument").evaluate(root, XPathConstants.NODE);
    if (c32doc == null)
    {
      String base64Encoded = (String) xPath.xpr("//xds-b:Document/text()").evaluate(root, XPathConstants.STRING);
      byte[] xmlDecoded = Base64.decode(base64Encoded.getBytes(Charsets.UTF_8));
      c32doc = docBuilder.parse(new ByteArrayInputStream(xmlDecoded));
    }
    if (c32doc == null)
    {
      throw new InvalidTransformationResultException("Node urn:hl7-org:v3:ClinicalDocument not found.");
    }
    return c32doc;
  }

}
