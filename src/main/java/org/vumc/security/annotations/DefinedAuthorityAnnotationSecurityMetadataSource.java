/* Project: continuum
 * File: DefinedAuthorityAnnotationSecurityMetadataSource.java
 * Created: Apr 24, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.security.annotations;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.ExpressionBasedAnnotationAttributeFactory;
import org.springframework.security.access.prepost.PrePostAnnotationSecurityMetadataSource;
import org.springframework.security.access.prepost.PrePostInvocationAttributeFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefinedAuthorityAnnotationSecurityMetadataSource extends
    PrePostAnnotationSecurityMetadataSource
{
  private final PrePostInvocationAttributeFactory attrFactory;

  private DefinedAuthorityAnnotationSecurityMetadataSource(PrePostInvocationAttributeFactory attrFactory)
  {
    super(attrFactory);
    this.attrFactory = attrFactory;
  }

  public DefinedAuthorityAnnotationSecurityMetadataSource()
  {
    this(
        new ExpressionBasedAnnotationAttributeFactory(
            new DefaultMethodSecurityExpressionHandler()
        )
    );
  }

  @Override
  public Collection<ConfigAttribute> getAttributes(final Method method, final Class<?> targetClass)
  {
    return Lists.newArrayList(Iterables.concat(
        super.getAttributes(method, targetClass),
        getExtraAttributes(method, targetClass)));
  }

  private Collection<ConfigAttribute> getExtraAttributes(final Method method, final Class<?> targetClass)
  {
    AllowedAuthorities allowedAuthorities = findAnnotation(method, targetClass, AllowedAuthorities.class);
    if (allowedAuthorities != null) {
      String hasAnyAuthorityParamList =
          Stream.of(allowedAuthorities.value())
              .map(GrantedAuthority::getAuthority)
              .map(a -> String.format("'%s'", a))
              .collect(Collectors.joining(","));
      return Collections.singletonList(
          attrFactory.createPreInvocationAttribute(null, null,
            String.format("hasAnyAuthority(%s)", hasAnyAuthorityParamList)
          ));
    }
    return Collections.emptyList();
  }

  private <A extends Annotation> A findAnnotation(Method method, Class<?> targetClass,
                                                  Class<A> annotationClass) {
    // The method may be on an interface, but we need attributes from the target
    // class.
    // If the target class is null, the method will be unchanged.
    Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
    A annotation = AnnotationUtils.findAnnotation(specificMethod, annotationClass);

    if (annotation != null) {
      logger.debug(annotation + " found on specific method: " + specificMethod);
      return annotation;
    }

    // Check the original (e.g. interface) method
    if (specificMethod != method) {
      annotation = AnnotationUtils.findAnnotation(method, annotationClass);

      if (annotation != null) {
        logger.debug(annotation + " found on: " + method);
        return annotation;
      }
    }

    // Check the class-level (note declaringClass, not targetClass, which may not
    // actually implement the method)
    annotation = AnnotationUtils.findAnnotation(specificMethod.getDeclaringClass(),
        annotationClass);

    if (annotation != null) {
      logger.debug(annotation + " found on: "
                   + specificMethod.getDeclaringClass().getName());
      return annotation;
    }

    return null;
  }
}
