package com.example.vanguard.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

public class ValidationUtils {

  public static void validateDateRange(LocalDate fromDate, LocalDate toDate) {
    if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
      throw new IllegalArgumentException("fromDate must not be after toDate");
    }
  }

  public static void validateSalePrice(BigDecimal salePrice) {
    if (salePrice != null && salePrice.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("salePrice must not be negative");
    }
  }

  public static void validatePageSize(int pageSize) {
    if (pageSize > 100) {
      throw new IllegalArgumentException("Page size must not exceed 100 records per request");
    }
  }

  public static Integer validateType(String value) {
    try {
      int type = Integer.parseInt(value);
      if (type < 0) {
        throw new IllegalArgumentException("Type must be a non-negative integer.");
      }
      return type;
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Invalid Type: " + value, e);
    }
  }

  public static ZonedDateTime validateDate(String value) {
    try {
      return ZonedDateTime.parse(value);
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("Invalid Date of Sale: " + value, e);
    }
  }

  public static Integer validateRange(Integer value, int min, int max, String fieldName) {
    if (value < min || value > max) {
      throw new IllegalArgumentException(fieldName + " must be between " + min + " and " + max);
    }
    return value;
  }

  public static String validateStringField(String value, String fieldName, int maxLength) {
    if (value == null || value.length() > maxLength) {
      throw new IllegalArgumentException(
          fieldName + " must not exceed " + maxLength + " characters");
    }
    return value;
  }

  public static BigDecimal validateBigDecimal(String value, String fieldName, BigDecimal maxValue) {
    try {
      BigDecimal bigDecimal = new BigDecimal(value);
      if (bigDecimal.compareTo(BigDecimal.ZERO) < 0) {
        throw new IllegalArgumentException(fieldName + " must be non-negative.");
      }
      if (bigDecimal.compareTo(maxValue) > 0) {
        throw new IllegalArgumentException(fieldName + " must not exceed " + maxValue + ".");
      }
      return bigDecimal;
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Invalid " + fieldName + ": " + value, e);
    }
  }
}
