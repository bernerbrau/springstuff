/* Project: continuum
 * File: ClientLogController.java
 * Created: Apr 04, 2017
 * Author: Josh Yarbrough - josh.yarbrough@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 *
 * ----  Example of JSON to send in body of POST request ----
 * {
 *      "level":"info",
 *      "msg":"This is a test from Postman using JSON.",
 *      "mrn":"123456789"
 * }
 *
 */

package org.vumc.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.vumc.model.ClientLogMessage;


@RestController
@RequestMapping("/api/clientlogger")
public class ClientLogController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientLogController.class);

    @Autowired
    public ClientLogController()
    {
    }

    @RequestMapping(method = RequestMethod.POST)
    public void logClientMsg(@RequestBody ClientLogMessage logMsg) throws Exception
    {
        String modifiedMsg = (logMsg.mrn != null && !logMsg.mrn.isEmpty() ? "MRN: " + logMsg.mrn + " - " + logMsg.msg : logMsg.msg);

        if(logMsg.level != null && !logMsg.level.isEmpty()){
            switch(logMsg.level.toUpperCase()){
                case "FATAL":
                case "ERROR":   LOGGER.error("{}",modifiedMsg);
                                break;
                case "WARN":    LOGGER.warn("{}",modifiedMsg);
                                break;
                case "INFO":    LOGGER.info("{}",modifiedMsg);
                                break;
                case "DEBUG":   LOGGER.debug("{}",modifiedMsg);
                                break;
                case "TRACE":   LOGGER.trace("{}",modifiedMsg);
                                break;
                default:        LOGGER.info("{}",modifiedMsg);
                                break;
            }
        }
        else{
            LOGGER.info("{}",modifiedMsg);
        }

    }

}
