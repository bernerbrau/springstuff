package org.vumc.transformations.c32;

import org.hibernate.engine.jdbc.NonContextualLobCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.vumc.controllers.PatientResourceController;
import org.vumc.model.RawMessage;

import java.time.ZonedDateTime;

@Component
public class RawC32RecordCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(PatientResourceController.class);

    public RawMessage createRawC32MessageObject(String inC32Request)
    {
        try {
            RawMessage rawMessage = new RawMessage();
            rawMessage.setReceived(ZonedDateTime.now());
            rawMessage.setRawMessage(NonContextualLobCreator.INSTANCE.createClob(inC32Request));
            return rawMessage;
        }
        catch (Exception e){
            LOGGER.info("createRawC32MessageObject failed for inC32Request: {}", inC32Request);
            return null;
        }
    }

}
