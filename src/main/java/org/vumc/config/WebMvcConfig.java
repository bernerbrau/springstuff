/* Project: spring
 * File: WebMvcConfig.java
 * Created: May 10, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport
{
  // Disable these default mappings to speed up application load time
  @Override
  public HandlerMapping viewControllerHandlerMapping()
  {
    return new AbstractHandlerMapping()
    {
      @Override
      protected Object getHandlerInternal(final HttpServletRequest inHttpServletRequest)
          throws Exception
      {
        return null;
      }
    };
  }

  @Override
  public BeanNameUrlHandlerMapping beanNameHandlerMapping()
  {
    return new BeanNameUrlHandlerMapping() {
      @Override
      protected String[] determineUrlsForHandler(final String beanName)
      {
        return new String[0];
      }
    };
  }
}
