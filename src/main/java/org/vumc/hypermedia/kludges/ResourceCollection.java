/* Project: continuum
 * File: ResourceCollection.java
 * Created: Apr 27, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.hypermedia.kludges;

import com.google.common.collect.Lists;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.hal.ResourcesMixin;
import org.springframework.util.Assert;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ResourceCollection<T> extends ResourcesMixin<T> implements Collection<T>
{

  private final Collection<T> content;

  public ResourceCollection(final Iterable<T> content, final Link... links)
  {
    this(content, Arrays.asList(links));
  }

  public ResourceCollection(final Iterable<T> content,
                            final Iterable<Link> links)
  {
    Assert.notNull(content);
    this.content = Lists.newArrayList(content);
    this.add(links);
  }

  @Override
  public Collection<T> getContent() {
    return Collections.unmodifiableCollection(content);
  }

  @Override
  public boolean equals(Object obj) {

    if (obj == this) {
      return true;
    }

    if (obj == null || !obj.getClass().equals(getClass())) {
      return false;
    }

    ResourceCollection<?> that = (ResourceCollection<?>) obj;

    boolean contentEqual = this.content == null ? that.content == null : this.content.equals(that.content);
    return contentEqual && super.equals(obj);
  }

  @Override
  public int hashCode() {

    int result = super.hashCode();
    result += content == null ? 0 : 17 * content.hashCode();

    return result;
  }

  public int size()
  {
    return content.size();
  }

  public boolean isEmpty()
  {
    return content.isEmpty();
  }

  public boolean contains(final Object o)
  {
    return content.contains(o);
  }

  public Iterator<T> iterator()
  {
    return content.iterator();
  }

  public Object[] toArray()
  {
    return content.toArray();
  }

  public <T1> T1[] toArray(final T1[] a)
  {
    return content.toArray(a);
  }

  public boolean add(final T inT)
  {
    return content.add(inT);
  }

  public boolean remove(final Object o)
  {
    return content.remove(o);
  }

  public boolean containsAll(final Collection<?> c)
  {
    return content.containsAll(c);
  }

  public boolean addAll(final Collection<? extends T> c)
  {
    return content.addAll(c);
  }

  public boolean removeAll(final Collection<?> c)
  {
    return content.removeAll(c);
  }

  public boolean removeIf(final Predicate<? super T> filter)
  {
    return content.removeIf(filter);
  }

  public boolean retainAll(final Collection<?> c)
  {
    return content.retainAll(c);
  }

  public void clear()
  {
    content.clear();
  }

  @Override
  public Spliterator<T> spliterator()
  {
    return content.spliterator();
  }

  public Stream<T> stream()
  {
    return content.stream();
  }

  public Stream<T> parallelStream()
  {
    return content.parallelStream();
  }


}
