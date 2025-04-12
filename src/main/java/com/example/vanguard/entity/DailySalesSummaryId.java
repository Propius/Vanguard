package com.example.vanguard.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class DailySalesSummaryId implements Serializable {

  private LocalDate dateOfSale;
  private Integer gameNo;

  public DailySalesSummaryId() {}

  public LocalDate getDateOfSale() {
    return dateOfSale;
  }

  public Integer getGameNo() {
    return gameNo;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DailySalesSummaryId that = (DailySalesSummaryId) o;
    return Objects.equals(dateOfSale, that.dateOfSale) && Objects.equals(gameNo, that.gameNo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dateOfSale, gameNo);
  }
}
