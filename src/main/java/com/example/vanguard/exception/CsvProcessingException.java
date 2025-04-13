package com.example.vanguard.exception;

public class CsvProcessingException extends RuntimeException {
  public CsvProcessingException(String message, Throwable cause) {
    super(message, cause);
  }
}
