/* Project: continuum
 * File: ClientAppController.java
 * Created: Mar 31, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class Angular2ForwardingController
{
    @RequestMapping( {"", "/",
                      "/login",
                      "/patients",
                      "/patients/{id:\\d+}" })
    public String index() {
      return "forward:/index.html";
    }
}
