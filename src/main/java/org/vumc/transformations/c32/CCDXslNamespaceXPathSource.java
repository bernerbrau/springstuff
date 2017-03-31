/* Project: continuum
 * File: CCDXslXPathSource.java
 * Created: Mar 31, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.transformations.c32;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.xml.SimpleNamespaceContext;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.Map;
import java.util.WeakHashMap;

@Component
class CCDXslNamespaceXPathSource
{
  private final Map<String, XPathExpression> xPathCache = new WeakHashMap<>();

  private final XPath xPath;

  @Autowired
  public CCDXslNamespaceXPathSource() throws Exception
  {
    xPath = XPathFactory.newInstance().newXPath();
    SimpleNamespaceContext ns = new SimpleNamespaceContext();
    ns.bindDefaultNamespaceUri("http://ww.w3.org/2000/xmlns");
    ns.bindNamespaceUri("xsl","http://www.w3.org/1999/XSL/Transform");
    ns.bindNamespaceUri("n1","urn:hl7-org:v3");
    xPath.setNamespaceContext(ns);
  }

  synchronized XPathExpression xpr(String expression) throws XPathExpressionException
  {
    XPathExpression xpr = xPathCache.get(expression);
    if (xpr == null) {
      xpr = xPath.compile(expression);
      xPathCache.put(expression, xpr);
    }
    return xpr;
  }
}