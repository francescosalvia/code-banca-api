package com.banca.project.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
@Slf4j
public class ControllerAdviceException extends ResponseEntityExceptionHandler {

  @ExceptionHandler(value = {Exception.class})
  protected ResponseEntity<Object> handleBadRequest(Exception ex, WebRequest request) {
    log.error(ex.getMessage(), ex);
    HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    ControllerExceptionResponse controllerExceptionResponse =
        new ControllerExceptionResponse(
            ex.getMessage(), "internal_server_error", ZonedDateTime.now());
    return handleExceptionInternal(
        ex, controllerExceptionResponse, new HttpHeaders(), httpStatus, request);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    List<DetailError> details = new ArrayList<>();
    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
      log.error(error.getDefaultMessage());
      details.add(new DetailError(error.getField(), error.getDefaultMessage()));
    }
    ControllerExceptionResponse controllerExceptionResponse =
        new ControllerExceptionResponse("Param Error", "param_error", details, ZonedDateTime.now());
    return handleExceptionInternal(
        ex, controllerExceptionResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(
      value = {InternalServerErrorException.class, SandboxInternalErrorException.class})
  protected ResponseEntity<Object> handleInternalServerError(
      InternalServerErrorException ex, WebRequest request) {
    log.error(ex.getMessage());
    HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    ControllerExceptionResponse controllerExceptionResponse =
        new ControllerExceptionResponse(ex.getMessage(), ex.getCode(), ZonedDateTime.now());
    return handleExceptionInternal(
        ex, controllerExceptionResponse, new HttpHeaders(), httpStatus, request);
  }

  @ExceptionHandler(
      value = {
        BadRequestException.class,
        CustomerIdNotCorrectlyException.class,
        EndDateNotCorrectlyException.class,
        GetBalanceBadRequestException.class,
        GetBalanceNullException.class,
        GetInfoAccountBadRequestException.class,
        GetInfoAccountNullException.class
      })
  protected ResponseEntity<Object> handleBadRequestError(
      BadRequestException ex, WebRequest request) {
    log.error(ex.getMessage());
    HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
    ControllerExceptionResponse controllerExceptionResponse =
        new ControllerExceptionResponse(ex.getMessage(), ex.getCode(), ZonedDateTime.now());
    return handleExceptionInternal(
        ex, controllerExceptionResponse, new HttpHeaders(), httpStatus, request);
  }
}
