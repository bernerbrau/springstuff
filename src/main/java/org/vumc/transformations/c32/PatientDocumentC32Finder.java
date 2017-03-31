/* Project: continuum
 * File: PatientDocumentC32Finder.java
 * Created: Mar 31, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.transformations.c32;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;

import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

@Component
class PatientDocumentC32Finder
{
  private CCDXslNamespaceXPathSource xPath;

  @Autowired
  public PatientDocumentC32Finder(CCDXslNamespaceXPathSource xPath)
  {
    this.xPath = xPath;
  }

  Node findC32Document(Node root) throws XPathExpressionException {
    Node c32doc = (Node) xPath.xpr("//n1:ClinicalDocument").evaluate(root, XPathConstants.NODE);
    if (c32doc == null) {
      throw new InvalidTransformationResultException("Node urn:hl7-org:v3:ClinicalDocument not found.");
    }
    return c32doc;
  }

}
