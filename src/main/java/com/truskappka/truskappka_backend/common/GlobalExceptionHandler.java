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
    ResponseEntity<ErrorResponse> handleObjectNotFoundException(ObjectNotFoundException exception) {
        return new ResponseEntity<>(new ErrorResponse(exception.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ObjectAlreadyExistsException.class)
    ResponseEntity<ErrorResponse> handleObjectAlreadyExistsException(ObjectAlreadyExistsException exception) {
        return new ResponseEntity<>(new ErrorResponse(exception.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ForbiddenAccessException.class)
    ResponseEntity<ErrorResponse> handleForbiddenAccessException(ForbiddenAccessException exception) {
        return new ResponseEntity<>(new ErrorResponse(exception.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UserNotVendorException.class)
    ResponseEntity<ErrorResponse> handleUserNotVendorException(UserNotVendorException exception) {
        return new ResponseEntity<>(new ErrorResponse(exception.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
