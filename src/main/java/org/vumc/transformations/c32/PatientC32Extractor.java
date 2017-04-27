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
import org.vumc.transformations.xml.XPathSource;
import org.w3c.dom.Node;

import fj.function.TryEffect0;
import fj.function.TryEffect1;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Component
class PatientC32Extractor
{
  private final XPathSource                   xPath;
  private final PatientC32DocumentTransformer transformer;

  @Autowired
  public PatientC32Extractor(final XPathSource xPath,
                             final PatientC32DocumentTransformer inTransformer)
  {
    this.xPath = xPath;
    transformer = inTransformer;
  }

  Patient extractPatient(Node c32Element, String xmlAsString)
      throws Exception
  {
      final Patient patient = new Patient();
      doInNode(c32Element, "/n1:ClinicalDocument/n1:recordTarget/n1:patientRole", roleNode -> {
          doForString(roleNode, "./n1:id/@extension", patient::setPatientId);
          doInNode(roleNode, "./n1:patient", patientNode -> {
              doInNode(patientNode, "./n1:name", nameNode -> {
                final PatientName name = new PatientName();
                doInNode(nameNode, "./n1:family",
                    family ->
                    {
                      doForString(nameNode, "./n1:family", name::setFamily);
                      doForString(nameNode, "./n1:given", val -> name.setGiven(val.trim()));
                      doForString(nameNode,"./n1:suffix",name::setSuffix);
                    },
                    () -> doForString(nameNode,"normalize-space(.)",name::setName)
                );
                patient.setName(name);
              });
              doForString(patientNode,"./n1:administrativeGenderCode/@displayName",patient::setGender);
              doForString(patientNode,"./n1:birthTime/@value",
                  dob -> patient.setDob(LocalDate.parse(dob, DateTimeFormatter.ofPattern("yyyyMMdd"))));
          });
      });
      patient.setBody(transformer.c32DocumentToHTML(c32Element));
      patient.setRawMessage(xmlAsString);
      patient.setCreated(ZonedDateTime.now());
      return patient;
  }

  private <E extends Exception>
  void doInNode(final Node node, final String path, TryEffect1<Node, E> action)
      throws XPathExpressionException, E
  {
    doInNode(node, path, action, () -> {System.out.println("Hello");});
  }

  private <E extends Exception>
  void doForString(final Node node, final String path, TryEffect1<String, E> action)
      throws XPathExpressionException, E
  {
    doForString(node, path, action, () -> {});
  }

  private <E1 extends Exception, E2 extends Exception>
    void doInNode(final Node node, final String path, TryEffect1<Node, E1> action, TryEffect0<E2> otherwiseAction)
      throws XPathExpressionException, E1, E2
  {
    Node subNode = (Node) xPath.xpr(path).evaluate(node, XPathConstants.NODE);
    if (subNode != null) {
        action.f(subNode);
    } else {
        otherwiseAction.f();
    }
  }

  private <E1 extends Exception, E2 extends Exception>
  void doForString(final Node node, final String path, TryEffect1<String, E1> action, TryEffect0<E2> otherwiseAction)
      throws XPathExpressionException, E1, E2
  {
    String value = (String) xPath.xpr(path).evaluate(node, XPathConstants.STRING);
    if (value != null && !value.isEmpty()) {
      action.f(value);
    } else {
      otherwiseAction.f();
    }
  }

}
