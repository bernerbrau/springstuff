package org.vumc.exceptions;

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

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponseMsg> customExceptionResponseMsg(BadCredentialsException ex) {
        return new ExceptionResponseMsg(HttpStatus.UNAUTHORIZED,"Username or password incorrect.", ex.getMessage()).toResponseEntity();
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResponseMsg> customExceptionResponseMsg(AccessDeniedException ex) {
        return new ExceptionResponseMsg(HttpStatus.FORBIDDEN,"The current user does not have sufficient privileges for the attempted resource or action.", ex.getMessage()).toResponseEntity();
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ExceptionResponseMsg> customExceptionResponseMsg(UsernameNotFoundException ex) {
        return new ExceptionResponseMsg(HttpStatus.NOT_FOUND,"The requested user was not found.", ex.getMessage()).toResponseEntity();
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ExceptionResponseMsg> customExceptionResponseMsg(AuthenticationException ex) {
        return new ExceptionResponseMsg(HttpStatus.UNAUTHORIZED,"Internal authentication failure. Please contact VHAN support.", ex.getMessage()).toResponseEntity();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponseMsg> customExceptionResponseMsg(IllegalArgumentException ex) {
        return new ExceptionResponseMsg(HttpStatus.BAD_REQUEST,"Application presented an invalid request. Please contact VHAN support.", ex.getMessage()).toResponseEntity();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionResponseMsg> customExceptionResponseMsg(DataIntegrityViolationException ex) {
        return new ExceptionResponseMsg(HttpStatus.BAD_REQUEST,"The information you submitted was invalid. Please make any necessary corrections and try again.", ex.getMessage()).toResponseEntity();
    }

    @ExceptionHandler(TransientDataAccessException.class)
    public ResponseEntity<ExceptionResponseMsg> customExceptionResponseMsg(TransientDataAccessException ex) {
        return new ExceptionResponseMsg(HttpStatus.SERVICE_UNAVAILABLE,"There was a problem communicating with the database. Please try again later.", ex.getMessage()).toResponseEntity();
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ExceptionResponseMsg> customExceptionResponseMsg(DataAccessException ex) {
        return new ExceptionResponseMsg(HttpStatus.INTERNAL_SERVER_ERROR,"There was a problem communicating with the database. Please contact VHAN support.", ex.getMessage()).toResponseEntity();
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ExceptionResponseMsg> customExceptionResponseMsg(Throwable ex) {
        return new ExceptionResponseMsg(HttpStatus.INTERNAL_SERVER_ERROR,"An unexpected error occurred. Please contact VHAN support.", ex.getMessage()).toResponseEntity();
    }

}
