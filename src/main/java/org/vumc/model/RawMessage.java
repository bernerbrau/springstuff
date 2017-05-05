package org.vumc.model;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
public class RawMessage {

    public static final char STATUS_RECEIVED  = 'R';
    public static final char STATUS_PROCESSED = 'P';
    public static final char STATUS_ERROR     = 'E';

    @Column
    private int processTries = 0;

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE,generator="C32_RECEIVED_SEQ")
    @SequenceGenerator(name="C32_RECEIVED_SEQ",sequenceName="C32_RECEIVED_SEQ",allocationSize=1)
    private long    id;

    @Column
    private ZonedDateTime received;

    @Column
    private ZonedDateTime accessed;

    @Lob
    private String rawMessage;

    @Column(length = 1)
    private char status = STATUS_RECEIVED;

    public static RawMessage create(String inC32Request)
    {
        RawMessage rawMessage = new RawMessage();
        rawMessage.received = ZonedDateTime.now();
        rawMessage.accessed = rawMessage.received;
        rawMessage.setRawMessage(inC32Request);
        rawMessage.processTries = 0;
        rawMessage.setReceivedStatus();
        return rawMessage;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ZonedDateTime getReceived() {
        return received;
    }

    public ZonedDateTime getAccessed()
    {
        return accessed;
    }

    public void updateAccessed()
    {
        accessed = ZonedDateTime.now();
    }
    public String getRawMessage() {
        return rawMessage;
    }

    public void setRawMessage(String rawMessage) {
        this.rawMessage = rawMessage;
    }

    public int getProcessTries()
    {
        return processTries;
    }

    public void incrementProcessTries()
    {
        processTries++;
    }

    public void setReceivedStatus()
    {
        status = STATUS_RECEIVED;
    }

    public void setProcessedStatus()
    {
        status = STATUS_PROCESSED;
    }

    public void setErrorStatus()
    {
        status = STATUS_ERROR;
    }

}
