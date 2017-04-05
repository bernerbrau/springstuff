package org.vumc.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.vumc.model.Patient;
import org.vumc.repository.PatientRepository;

import java.util.List;

@RepositoryRestController
@RequestMapping("/api/patients")
@ExposesResourceFor(Patient.class)
public class PatientResourceController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PatientResourceController.class);

    private final PatientRepository repository;

    @Autowired
    public PatientResourceController(PatientRepository repository) {
        this.repository = repository;
    }

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<Resources<Resource<Patient>>> findAll() {
        LOGGER.info("Finding patients");
        return ResponseEntity.ok(Resources.wrap(repository.findAll()));
    }
}