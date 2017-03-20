/* Project: continuum
 * File: EmbeddableResourceAssemblerSupport.java
 * Created: Mar 20, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.hypermedia;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.google.common.collect.Iterables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.*;
import org.springframework.hateoas.core.EmbeddedWrapper;
import org.springframework.hateoas.core.EmbeddedWrappers;
import org.springframework.hateoas.core.Relation;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

abstract class EmbeddableResourceAssemblerSupport<T, D extends ResourceSupport, C> extends
    ResourceAssemblerSupport<T, D>
{

  final RelProvider relProvider;
  final EntityLinks entityLinks;
  final Class<C>    controllerClass;

  @Autowired
  public EmbeddableResourceAssemblerSupport(
      final EntityLinks entityLinks, final RelProvider relProvider,
      Class<C> controllerClass, Class<D> resourceType) {
    super(controllerClass, resourceType);
    this.entityLinks = entityLinks;
    this.relProvider = relProvider;
    this.controllerClass = controllerClass;
  }

  /**
   * Create a wrapped representation of a collection, to be added to the _embedded Resources of the containing resource
   * It relies on the embedded resource being annotated with {@link Relation}
   * and the collection of embedded objects being annotated with {@link JsonUnwrapped}
   */
  public List<EmbeddedWrapper> toEmbeddable(Iterable<T> entities) {
    final EmbeddedWrappers wrapper = new EmbeddedWrappers(true); // Prefer collection
    return toResources(entities).stream().map(wrapper::wrap).collect(Collectors.toList());
  }

  /**
   * Create a wrapped representation of a single object, to be added to the _embedded Resources of the containing resource
   */
  public EmbeddedWrapper toEmbeddable(T entity) {
    final EmbeddedWrappers wrapper = new EmbeddedWrappers(false); // DO NOT prefer collections
    final D resource = toResource(entity);
    return wrapper.wrap(resource);
  }

  /**
   * Create an empty main object wrapping a list of resources.
   * This is the way HAL expects endpoint returning a collection works
   */
  public Resources<D> toEmbeddedList(Iterable<T> entities) {
    final List<D> resources = toResources(entities);
    return new Resources<D>(resources, linkTo(controllerClass).withSelfRel()); // Add self link to list endpoint
  }

  public abstract Link linkToSingleResource(T entity);
}