/* Project: continuum
 * File: MDCFilter.java
 * Created: Apr 04, 2017
 * Author: Josh Yarbrough - josh.yarbrough@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.logging;

/*
https://moelholm.com/2016/08/16/spring-boot-enhance-your-logging/
https://logback.qos.ch/manual/mdc.html
https://logback.qos.ch/xref/chapters/mdc/UserServletFilter.html
https://logback.qos.ch/xref/ch/qos/logback/classic/helpers/MDCInsertingServletFilter.html
 */

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Principal;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

//import javax.servlet.http.HttpSession;

@Component
public class MDCFilter implements Filter {

    private final String USER_KEY = "userName";
    private final String CLIENT_IP_KEY = "clientIp";
    private final String CLIENT_HOST_KEY = "clientHost";

    @Override
    public void init(FilterConfig arg0) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        boolean successfulRegistration = false;

        HttpServletRequest req = (HttpServletRequest) request;
        Principal principal = req.getUserPrincipal();

        if (principal != null) {
            String username = principal.getName();
            successfulRegistration = registerContext(username, getHostName(), getCallerIP(req));
        }
        try {
            chain.doFilter(request, response);
        } finally {
            if (successfulRegistration) {
                unregisterContext();
            }
        }
    }

    private boolean registerContext(String username, String clientHost, String clientIp) {
        if (username != null && username.trim().length() > 0) {
            MDC.put(USER_KEY, username);
            MDC.put(CLIENT_HOST_KEY, clientHost);
            MDC.put(CLIENT_IP_KEY, clientIp);
            return true;
        }
        return false;
    }

    private void unregisterContext()
    {
        MDC.remove(USER_KEY);
        MDC.remove(CLIENT_HOST_KEY);
        MDC.remove(CLIENT_IP_KEY);
    }

    private String getHostName()
    {
        String ret;

        try
        {
            ret = InetAddress.getLocalHost().getHostName();
            int pos = ret.indexOf(".");
            if (pos != -1)
            {
                ret = ret.substring(0, pos);
            }
        }
        catch (UnknownHostException var2)
        {
            ret = "unknown";
        }

        return ret;
    }

    private String getCallerIP(HttpServletRequest inRequest)
    {
        String retVal;
        String xForwardedFor = inRequest.getHeader("X-Forwarded-For");
        if (xForwardedFor != null)
        {
            retVal = xForwardedFor;
        }
        else
        {
            retVal = inRequest.getRemoteAddr();
        }
        return retVal;
    }
}