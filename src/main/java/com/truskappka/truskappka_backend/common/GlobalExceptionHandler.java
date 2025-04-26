package com.truskappka.truskappka_backend.common;

import com.truskappka.truskappka_backend.common.exception.ForbiddenAccessException;
import com.truskappka.truskappka_backend.common.exception.ObjectAlreadyExistsException;
import com.truskappka.truskappka_backend.common.exception.ObjectNotFoundException;
import com.truskappka.truskappka_backend.user.exception.UserNotVendorException;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Hidden
class GlobalExceptionHandler {

    @ExceptionHandler(ObjectNotFoundException.class)
    ResponseEntity<String> handleObjectNotFoundException(ObjectNotFoundException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ObjectAlreadyExistsException.class)
    ResponseEntity<String> handleObjectAlreadyExistsException(ObjectAlreadyExistsException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ForbiddenAccessException.class)
    ResponseEntity<String> handleForbiddenAccessException(ForbiddenAccessException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UserNotVendorException.class)
    ResponseEntity<String> handleUserNotVendorException(UserNotVendorException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

}
