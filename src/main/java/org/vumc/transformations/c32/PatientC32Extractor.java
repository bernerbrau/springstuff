/* Project: continuum
 * File: PatientC32Extractor.java
 * Created: Mar 31, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.transformations.c32;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vumc.model.Patient;
import org.vumc.model.PatientName;
import org.w3c.dom.Node;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

@Component
class PatientC32Extractor
{
  private final CCDXslNamespaceXPathSource xPath;
  private final PatientC32DocumentTransformer transformer;

  @Autowired
  public PatientC32Extractor(final CCDXslNamespaceXPathSource xPath,
                             final PatientC32DocumentTransformer inTransformer)
  {
    this.xPath = xPath;
    transformer = inTransformer;
  }

  Patient extractPatient(Node c32Element) throws XPathExpressionException, TransformerException, IOException {
    Node patientRole = (Node) xPath.xpr("/n1:ClinicalDocument/n1:recordTarget/n1:patientRole").evaluate(c32Element, XPathConstants.NODE);
    String gender = (String) xPath.xpr("/n1:ClinicalDocument/n1:recordTarget/n1:patientRole/n1:patient/n1:administrativeGenderCode/@displayName").evaluate(c32Element, XPathConstants.STRING);

    Patient patient = new Patient();
    patient.patientId = (String) xPath.xpr("./n1:id/@extension").evaluate(patientRole, XPathConstants.STRING);
    patient.name = getNameJSON((Node) xPath.xpr("./n1:patient/n1:name").evaluate(patientRole, XPathConstants.NODE));
    patient.gender = gender;
    String strDob = (String) xPath.xpr("./n1:patient/n1:birthTime/@value").evaluate(patientRole, XPathConstants.STRING);
    if (strDob != null && !strDob.isEmpty())
    {
      patient.dob = LocalDate.parse(strDob, getDOBFormatter());
    }

    patient.body = transformAndSerialize(c32Element);

    return patient;
  }

  private String transformAndSerialize(final Node inC32Element) throws TransformerException,
                                                                       IOException
  {
    return transformer.c32DocumentToHTML(inC32Element);
  }

  private PatientName getNameJSON(final Node nameNode) throws XPathExpressionException
  {
    PatientName name = new PatientName();
    String family = (String) xPath.xpr("./n1:family").evaluate(nameNode, XPathConstants.STRING);
    if (family != null) {
      name.family = family;
      String given = (String) xPath.xpr("./n1:given").evaluate(nameNode, XPathConstants.STRING);
      if (given != null) {
        name.given = given;
      }
      String suffix = (String) xPath.xpr("./n1:suffix").evaluate(nameNode, XPathConstants.STRING);
      if (suffix != null) {
        name.suffix = suffix;
      }
    } else {
      name.name = (String) xPath.xpr("normalize-space(.)").evaluate(nameNode, XPathConstants.STRING);
    }
    return name;
  }

  private DateTimeFormatter getDOBFormatter()
  {
    return DateTimeFormatter.ofPattern("yyyyMMdd");
  }

}
