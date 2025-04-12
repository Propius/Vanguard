package com.example.vanguard.common.enumeration;

public enum FilterType {
  LESS_THAN("lessThan"),
  GREATER_THAN("greaterThan");

  private final String value;

  FilterType(String value) {
    this.value = value;
  }

  public static FilterType fromValue(String value) {
    for (FilterType type : FilterType.values()) {
      if (type.value.equals(value)) {
        return type;
      }
    }
    throw new IllegalArgumentException("Unknown filter type: ".concat(value));
  }

  public String getValue() {
    return value;
  }
}
