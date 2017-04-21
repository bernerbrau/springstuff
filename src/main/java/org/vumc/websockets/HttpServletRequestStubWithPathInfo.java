/* Project: continuum
 * File: HttpServletRequestStubWithPathInfoAttributesAndHeaders.java
 * Created: Apr 21, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.websockets;

import com.google.common.collect.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.*;

public class HttpServletRequestStubWithPathInfo implements HttpServletRequest
{

  private final String                   method;
  private final String                   pathInfo;
  private final String                   pathTranslated;
  private final String                   contextPath;
  private final String                   queryString;
  private final String                   requestURI;
  private final StringBuffer             requestURL;
  private final String                   servletPath;
  private final String                   protocol;
  private final String                   scheme;
  private final String                   serverName;
  private final String                   remoteAddr;
  private final String                   remoteHost;
  private final String                   localName;
  private final int                      remotePort;
  private final int                      localPort;
  private final String                   localAddr;
  private final Multimap<String, String> params;
  private final Multimap<String, String> headers;

  public HttpServletRequestStubWithPathInfo(final HttpServletRequest inServletRequest)
  {
    method = inServletRequest.getMethod();
    pathInfo = inServletRequest.getPathInfo();
    pathTranslated = inServletRequest.getPathTranslated();
    contextPath = inServletRequest.getContextPath();
    queryString = inServletRequest.getQueryString();
    requestURI = inServletRequest.getRequestURI();
    requestURL = inServletRequest.getRequestURL();
    servletPath = inServletRequest.getServletPath();
    protocol = inServletRequest.getProtocol();
    scheme = inServletRequest.getScheme();
    serverName = inServletRequest.getServerName();
    remoteAddr = inServletRequest.getRemoteAddr();
    remoteHost = inServletRequest.getRemoteHost();
    localName = inServletRequest.getLocalName();
    remotePort = inServletRequest.getRemotePort();
    localPort = inServletRequest.getLocalPort();
    localAddr = inServletRequest.getLocalAddr();

    params = Multimaps.newListMultimap(
        new LinkedHashMap<>(),
        ArrayList::new);
    Maps.transformValues(inServletRequest.getParameterMap(), Arrays::asList)
        .forEach(params::putAll);

    headers = Multimaps.newListMultimap(
        new LinkedHashMap<>(),
        ArrayList::new);
    Maps.toMap(
        Iterators.forEnumeration(inServletRequest.getHeaderNames()),
        n -> ImmutableList.copyOf(Iterators.forEnumeration(inServletRequest.getHeaders(n)))
    ).forEach(headers::putAll);
  }

  @Override
  public String getAuthType()
  {
    return null;
  }

  @Override
  public Cookie[] getCookies()
  {
    return new Cookie[0];
  }

  @Override
  public long getDateHeader(final String name)
  {
    String value = getHeader(name);
    if (value == null) {
      return -1L;
    }
    TemporalAccessor date = DateTimeFormatter.BASIC_ISO_DATE.parse(value);
    return date.getLong(ChronoField.INSTANT_SECONDS) * 1000
           + date.get(ChronoField.MILLI_OF_SECOND);
  }

  @Override
  public String getHeader(final String name)
  {
    return Iterables.getFirst(headers.get(name), null);
  }

  @Override
  public Enumeration<String> getHeaders(final String name)
  {
    return Iterators.asEnumeration(headers.get(name).iterator());
  }

  @Override
  public Enumeration<String> getHeaderNames()
  {
    return Iterators.asEnumeration(headers.keySet().iterator());
  }

  @Override
  public int getIntHeader(final String name)
  {
    String value = getHeader(name);
    if (value == null) {
      return -1;
    }

    return Integer.parseInt(name);
  }

  @Override
  public String getMethod()
  {
    return method;
  }

  @Override
  public String getPathInfo()
  {
    return pathInfo;
  }

  @Override
  public String getPathTranslated()
  {
    return pathTranslated;
  }

  @Override
  public String getContextPath()
  {
    return contextPath;
  }

  @Override
  public String getQueryString()
  {
    return queryString;
  }

  @Override
  public String getRemoteUser()
  {
    return null;
  }

  @Override
  public boolean isUserInRole(final String role)
  {
    return false;
  }

  @Override
  public Principal getUserPrincipal()
  {
    return null;
  }

  @Override
  public String getRequestedSessionId()
  {
    return null;
  }

  @Override
  public String getRequestURI()
  {
    return requestURI;
  }

  @Override
  public StringBuffer getRequestURL()
  {
    return requestURL;
  }

  @Override
  public String getServletPath()
  {
    return servletPath;
  }

  @Override
  public HttpSession getSession(final boolean create)
  {
    return null;
  }

  @Override
  public HttpSession getSession()
  {
    return null;
  }

  @Override
  public String changeSessionId()
  {
    return null;
  }

  @Override
  public boolean isRequestedSessionIdValid()
  {
    return false;
  }

  @Override
  public boolean isRequestedSessionIdFromCookie()
  {
    return false;
  }

  @Override
  public boolean isRequestedSessionIdFromURL()
  {
    return false;
  }

  @Override
  public boolean isRequestedSessionIdFromUrl()
  {
    return false;
  }

  @Override
  public boolean authenticate(final HttpServletResponse response)
      throws IOException, ServletException
  {
    return false;
  }

  @Override
  public void login(final String username, final String password) throws ServletException
  {

  }

  @Override
  public void logout() throws ServletException
  {

  }

  @Override
  public Collection<Part> getParts() throws IOException, ServletException
  {
    return null;
  }

  @Override
  public Part getPart(final String name) throws IOException, ServletException
  {
    return null;
  }

  @Override
  public <T extends HttpUpgradeHandler> T upgrade(final Class<T> httpUpgradeHandlerClass)
      throws IOException, ServletException
  {
    return null;
  }

  @Override
  public Object getAttribute(final String name)
  {
    return null;
  }

  @Override
  public Enumeration<String> getAttributeNames()
  {
    return null;
  }

  @Override
  public String getCharacterEncoding()
  {
    return null;
  }

  @Override
  public void setCharacterEncoding(final String env) throws UnsupportedEncodingException
  {

  }

  @Override
  public int getContentLength()
  {
    return 0;
  }

  @Override
  public long getContentLengthLong()
  {
    return 0;
  }

  @Override
  public String getContentType()
  {
    return null;
  }

  @Override
  public ServletInputStream getInputStream() throws IOException
  {
    return null;
  }

  @Override
  public String getParameter(final String name)
  {
    return Iterables.getFirst(params.get(name), null);
  }

  @Override
  public Enumeration<String> getParameterNames()
  {
    return Iterators.asEnumeration(params.keySet().iterator());
  }

  @Override
  public String[] getParameterValues(final String name)
  {
    return params.get(name).toArray(new String[0]);
  }

  @Override
  public Map<String, String[]> getParameterMap()
  {
    return Maps.transformValues(params.asMap(), c -> c == null
                                                         ? new String[0]
                                                         : c.toArray(new String[0]));
  }

  @Override
  public String getProtocol()
  {
    return protocol;
  }

  @Override
  public String getScheme()
  {
    return scheme;
  }

  @Override
  public String getServerName()
  {
    return serverName;
  }

  @Override
  public int getServerPort()
  {
    return 0;
  }

  @Override
  public BufferedReader getReader() throws IOException
  {
    return null;
  }

  @Override
  public String getRemoteAddr()
  {
    return remoteAddr;
  }

  @Override
  public String getRemoteHost()
  {
    return remoteHost;
  }

  @Override
  public void setAttribute(final String name, final Object o)
  {

  }

  @Override
  public void removeAttribute(final String name)
  {

  }

  @Override
  public Locale getLocale()
  {
    return null;
  }

  @Override
  public Enumeration<Locale> getLocales()
  {
    return null;
  }

  @Override
  public boolean isSecure()
  {
    return false;
  }

  @Override
  public RequestDispatcher getRequestDispatcher(final String path)
  {
    return null;
  }

  @Override
  public String getRealPath(final String path)
  {
    return null;
  }

  @Override
  public int getRemotePort()
  {
    return remotePort;
  }

  @Override
  public String getLocalName()
  {
    return localName;
  }

  @Override
  public String getLocalAddr()
  {
    return localAddr;
  }

  @Override
  public int getLocalPort()
  {
    return localPort;
  }

  @Override
  public ServletContext getServletContext()
  {
    return null;
  }

  @Override
  public AsyncContext startAsync() throws IllegalStateException
  {
    return null;
  }

  @Override
  public AsyncContext startAsync(final ServletRequest servletRequest,
                                 final ServletResponse servletResponse)
      throws IllegalStateException
  {
    return null;
  }

  @Override
  public boolean isAsyncStarted()
  {
    return false;
  }

  @Override
  public boolean isAsyncSupported()
  {
    return false;
  }

  @Override
  public AsyncContext getAsyncContext()
  {
    return null;
  }

  @Override
  public DispatcherType getDispatcherType()
  {
    return null;
  }
}
