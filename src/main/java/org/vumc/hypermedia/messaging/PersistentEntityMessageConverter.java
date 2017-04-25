/* Project: continuum
 * File: PersistentEntityMessageConverter.java
 * Created: Mar 31, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.hypermedia.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.rest.webmvc.HttpHeadersPreparer;
import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.hateoas.mvc.ResourceProcessorInvoker;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PersistentEntityMessageConverter extends MappingJackson2MessageConverter
{
  private static final String APPLICATION_TYPE              = "application";
  private static final String HAL_JSON_SUBTYPE              = "hal+json";
  private static final String APPLICATION_HAL_JSON_FULLTYPE =
      APPLICATION_TYPE + "/" + HAL_JSON_SUBTYPE;

  private final ResourceProcessorInvoker                           processors;
  private final PersistentEntityResourceAssembler                  assembler;
  private final HttpHeadersPreparer                                preparer;

  public PersistentEntityMessageConverter(final ResourceProcessorInvoker inProcessors,
                                          final PersistentEntityResourceAssembler inAssembler,
                                          @Qualifier("halObjectMapper")
                                          final ObjectMapper inObjectMapper,
                                          final HttpHeadersPreparer inPreparer)
  {
    setObjectMapper(inObjectMapper);
    processors = inProcessors;
    assembler = inAssembler;
    preparer = inPreparer;
  }

  @Override
  protected Object convertToInternal(final Object payload, final MessageHeaders headers,
                                     final Object conversionHint)
  {
    PersistentEntityResource resource =
        processors.invokeProcessorsFor(
            assembler.toFullResource(payload));

    HttpHeaders httpHeaders = preparer.prepareHeaders(resource);

    final Map<String, Object> headerValues = new HashMap<>();
    headerValues.putAll(
        httpHeaders.entrySet()
            .stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                (entry) -> entry.getValue().get(0)))
    );
    headerValues.putAll(headers);
    headerValues.put(MessageHeaders.CONTENT_TYPE, APPLICATION_HAL_JSON_FULLTYPE);

    MessageHeaders newHeaders = new MessageHeaders(headerValues);

    MessageHeaderAccessor
        accessor = MessageHeaderAccessor.getAccessor(headers, MessageHeaderAccessor.class);
    if (accessor != null && accessor.isMutable()) {
      accessor.copyHeadersIfAbsent(newHeaders);
    }

    return super.convertToInternal(resource, newHeaders, conversionHint);
  }

  @Override
  protected Object convertFromInternal(final Message<?> message, final Class<?> targetClass,
                                       final Object conversionHint)
  {
    PersistentEntityResource resource =
        (PersistentEntityResource) super.convertFromInternal(message, targetClass, conversionHint);
    return resource.getContent();
  }

}
