package org.vumc.hypermedia.resources;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.core.EmbeddedWrapper;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

abstract class ResourceWithEmbeddeds extends ResourceSupport {
   
   @JsonUnwrapped 
   // The @JsonUnwrapped annotation is required as EmbeddedWrappers are by default serialised in a "_embedded" container, 
   // that has to be added directly into the top level object
   private Resources<EmbeddedWrapper> embeddeds;

   public Resources<EmbeddedWrapper> getEmbeddeds() {
      return embeddeds;
   }

   public void setEmbeddeds(Resources<EmbeddedWrapper> embeddeds) {
      this.embeddeds = embeddeds;
   }   
}