package org.vumc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;
import java.sql.Clob;
import java.time.ZonedDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Entity
@JsonInclude(NON_NULL)
public class RawMessage {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE,generator="C32_RECEIVED_SEQ")
    @SequenceGenerator(name="C32_RECEIVED_SEQ",sequenceName="C32_RECEIVED_SEQ",allocationSize=1)
    private long    id;

    @Column
    private ZonedDateTime received;

    @Lob
    @JsonIgnore
    private Clob rawMessage;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ZonedDateTime getReceived() {
        return received;
    }

    public void setReceived(ZonedDateTime received) {
        this.received = received;
    }

    public Clob getRawMessage() {
        return rawMessage;
    }

    public void setRawMessage(Clob rawMessage) {
        this.rawMessage = rawMessage;
    }

}
