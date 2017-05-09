/* Project: continuum
 * File: BeanRegistrationConfig.java
 * Created: Apr 28, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class BeanRegistrationConfig implements BeanDefinitionRegistryPostProcessor
{
  @Override
  public void postProcessBeanDefinitionRegistry(final BeanDefinitionRegistry registry)
      throws BeansException
  {
    // "When in doubt, rip it out."
    // As a last resort for unwanted Spring behavior we can't otherwise change,
    //   exclude the Spring Beans that provide this behavior
    try
    {
      registry.removeBeanDefinition("errorPageFilter");
    } catch (NoSuchBeanDefinitionException ex) {}
    try
    {
      registry.removeBeanDefinition("profileController");
    } catch (NoSuchBeanDefinitionException ex) {}
    try
    {
      registry.removeBeanDefinition("profileResourceProcessor");
    } catch (NoSuchBeanDefinitionException ex) {}
  }

  @Override
  public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory)
      throws BeansException
  {

  }
}
