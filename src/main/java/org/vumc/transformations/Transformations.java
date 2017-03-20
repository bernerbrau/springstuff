/* Project: continuum
 * File: Transformations.java
 * Created: Mar 17, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.transformations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import rx.functions.Func1;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.text.ParseException;
import java.util.concurrent.Callable;
import java.util.function.Function;

@Component
public class Transformations
{
  private final XPathExpression c32DocExpr;
  private Transformer     patientJsonXslTransformer;
  private ObjectMapper    objectMapper;
  private XPath           xPath;

  @Autowired
  public Transformations(@Qualifier("cdaTransformer") final Transformer inPatientJsonXslTransformer,
                         final ObjectMapper inObjectMapper,
                         final XPath inXPath) throws Exception
  {
    patientJsonXslTransformer = inPatientJsonXslTransformer;
    objectMapper = inObjectMapper;
    xPath = inXPath;
    c32DocExpr =
        xPath.compile("//*[local-name()='ClinicalDocument' and namespace-uri()='urn:hl7-org:v3']");
  }

  public Node extractC32Document(String raw)
  {
    return uncheck(
        () -> {
          Node c32doc = (Node) c32DocExpr.evaluate(new InputSource(new StringReader(raw)), XPathConstants.NODE);
          if (c32doc == null) {
            throw new InvalidTransformationResultException("Node urn:hl7-org:v3:ClinicalDocument not found.");
          }
          return c32doc;
        });
  }

  public String c32DocumentToPatientJsonString(Node xml)
  {
    return uncheck(() ->
    {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      patientJsonXslTransformer.transform(
          new DOMSource(xml),
          new StreamResult(baos));
      baos.flush();
      return new String(baos.toByteArray(), Charsets.UTF_8);
    });
  }

  public String checkJsonForErrorResult(String json) {
    return uncheck(() ->
    {
      ErrorResult result = objectMapper.readValue(json, ErrorResult.class);
      if (result.error != null)
      {
        throw new InvalidTransformationResultException(result.error);
      }
      return json;
    });
  }

  public <T> Function<String, T> jsonToObject(Class<T> objectClass) {
    return json -> jsonToObject(json, objectClass);
  }

  public <T> T jsonToObject(String json, Class<T> objectClass) {
    return uncheck(() -> objectMapper.readValue(json, objectClass));
  }

  // NOTE: This abuses a compiler bug, causing it to miss checked exceptions.
  // We do this ONLY to make Rx streams easier to write.
  // This does NOT absolve the developer from handling errors in the Rx stream.
  private <R> R uncheck(Callable<R> func)
  {
    return this.doUncheck(func);
  }

  @SuppressWarnings("unchecked")
  private <R, T extends Throwable> R doUncheck(Callable<R> func) throws T
  {
    try
    {
      return func.call();
    }
    catch (Throwable t)
    {
      throw (T) t;
    }
  }

}
