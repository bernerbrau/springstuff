/* Project: continuum
 * File: XPathSource.java
 * Created: Apr 04, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.transformations.xml;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

public interface XPathSource
{
  XPathExpression xpr(String expression) throws XPathExpressionException;
}
