package org.vumc.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ExceptionResponseMsg {
    private int status;
    private String message;
    private String developerMessage;

    public ExceptionResponseMsg(HttpStatus status, String message, String developerMessage) {
        this.status = status.value();
        this.message = message;
        this.developerMessage = developerMessage;
    }

    public ResponseEntity<ExceptionResponseMsg> toResponseEntity() {
        return ResponseEntity.status(status).body(this);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDeveloperMessage() {
        return developerMessage;
    }

    public void setDeveloperMessage(String developerMessage) {
        this.developerMessage = developerMessage;
    }

}
