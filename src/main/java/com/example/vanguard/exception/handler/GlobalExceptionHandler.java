package com.example.vanguard.exception.handler;

import com.example.vanguard.exception.CsvProcessingException;
import java.util.concurrent.TimeoutException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

  @ExceptionHandler(CsvProcessingException.class)
  public ResponseEntity<String> handleCsvProcessingException(CsvProcessingException ex) {
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ex.getMessage());
  }

  @ExceptionHandler(TimeoutException.class)
  public ResponseEntity<String> handleTimeoutException(TimeoutException ex) {
    return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
        .body("A timeout occurred while processing the request: " + ex.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleGeneralException(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("An unexpected error occurred: " + ex.getMessage());
  }
}
