package org.vumc.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
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

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ExceptionResponseMsg> customExceptionResponseMsg(AuthenticationException ex) {
        return new ExceptionResponseMsg(HttpStatus.UNAUTHORIZED,"Internal authentication failure. Please contact VHAN support.", ex.getMessage()).toResponseEntity();
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ExceptionResponseMsg> customExceptionResponseMsg(Throwable ex) {
        return new ExceptionResponseMsg(HttpStatus.INTERNAL_SERVER_ERROR,"An unexpected error occurred. Please contact VHAN support.", ex.getMessage()).toResponseEntity();
    }

}
