package com.example.vanguard.entity;

import java.io.Serializable;
import java.util.Objects;

public class CombinedSalesSummaryId implements Serializable {

  private String periodOfSale;
  private Integer gameNo;

  public CombinedSalesSummaryId() {}

  public String getPeriodOfSale() {
    return periodOfSale;
  }

  public Integer getGameNo() {
    return gameNo;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CombinedSalesSummaryId that = (CombinedSalesSummaryId) o;
    return Objects.equals(periodOfSale, that.periodOfSale) && Objects.equals(gameNo, that.gameNo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(periodOfSale, gameNo);
  }
}
