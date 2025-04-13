package com.example.vanguard.common.enumeration;

public enum PeriodFilterType {
  HOURLY("hourly"),
  DAILY("daily"),
  WEEKLY("weekly"),
  MONTHLY("monthly"),
  QUARTERLY("quarterly"),
  YEARLY("yearly");

  private final String value;

  PeriodFilterType(String value) {
    this.value = value;
  }

  public static PeriodFilterType fromValue(String value) {
    for (PeriodFilterType type : PeriodFilterType.values()) {
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
