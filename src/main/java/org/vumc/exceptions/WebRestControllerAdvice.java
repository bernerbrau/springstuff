package org.vumc.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@RestControllerAdvice
public class WebRestControllerAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebRestControllerAdvice.class);

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponseMsg> customExceptionResponseMsg(BadCredentialsException ex) {
        LOGGER.error("Bad Credentials", ex);
        return new ExceptionResponseMsg(HttpStatus.UNAUTHORIZED,"Username or password incorrect.", ex.getMessage()).toResponseEntity();
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResponseMsg> customExceptionResponseMsg(AccessDeniedException ex) {
        LOGGER.error("Access Denied", ex);
        return new ExceptionResponseMsg(HttpStatus.FORBIDDEN,"The current user does not have sufficient privileges for the attempted resource or action.", ex.getMessage()).toResponseEntity();
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ExceptionResponseMsg> customExceptionResponseMsg(UsernameNotFoundException ex) {
        LOGGER.error("Username Not Found", ex);
        return new ExceptionResponseMsg(HttpStatus.NOT_FOUND,"The requested user was not found.", ex.getMessage()).toResponseEntity();
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ExceptionResponseMsg> customExceptionResponseMsg(AuthenticationException ex) {
        LOGGER.error("Unauthorized", ex);
        return new ExceptionResponseMsg(HttpStatus.UNAUTHORIZED,"Internal authentication failure. Please contact VHAN support.", ex.getMessage()).toResponseEntity();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponseMsg> customExceptionResponseMsg(IllegalArgumentException ex) {
        LOGGER.error("Bad Request", ex);
        return new ExceptionResponseMsg(HttpStatus.BAD_REQUEST,"Application presented an invalid request. Please contact VHAN support.", ex.getMessage()).toResponseEntity();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionResponseMsg> customExceptionResponseMsg(DataIntegrityViolationException ex) {
        LOGGER.error("Data Integrity Violation", ex);
        return new ExceptionResponseMsg(HttpStatus.BAD_REQUEST,"The information you submitted was invalid. Please make any necessary corrections and try again.", ex.getMessage()).toResponseEntity();
    }

    @ExceptionHandler(TransientDataAccessException.class)
    public ResponseEntity<ExceptionResponseMsg> customExceptionResponseMsg(TransientDataAccessException ex) {
        LOGGER.error("Transient Data Exception", ex);
        return new ExceptionResponseMsg(HttpStatus.SERVICE_UNAVAILABLE,"There was a problem communicating with the database. Please try again later.", ex.getMessage()).toResponseEntity();
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ExceptionResponseMsg> customExceptionResponseMsg(DataAccessException ex) {
        LOGGER.error("Data Exception", ex);
        return new ExceptionResponseMsg(HttpStatus.INTERNAL_SERVER_ERROR,"There was a problem communicating with the database. Please contact VHAN support.", ex.getMessage()).toResponseEntity();
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ExceptionResponseMsg> customExceptionResponseMsg(Throwable ex) {
        LOGGER.error("Unexpected Error", ex);
        return new ExceptionResponseMsg(HttpStatus.INTERNAL_SERVER_ERROR,"An unexpected error occurred. Please contact VHAN support.", ex.getMessage()).toResponseEntity();
    }

}
