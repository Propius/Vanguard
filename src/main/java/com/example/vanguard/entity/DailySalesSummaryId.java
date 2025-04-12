package com.example.vanguard.entity;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

public class DailySalesSummaryId implements Serializable {

  private ZonedDateTime dateOfSale;
  private Integer gameNo;

  public DailySalesSummaryId() {}

  public DailySalesSummaryId(ZonedDateTime dateOfSale, Integer gameNo) {
    this.dateOfSale = dateOfSale;
    this.gameNo = gameNo;
  }

  public ZonedDateTime getDateOfSale() {
    return dateOfSale;
  }

  public void setDateOfSale(ZonedDateTime dateOfSale) {
    this.dateOfSale = dateOfSale;
  }

  public Integer getGameNo() {
    return gameNo;
  }

  public void setGameNo(Integer gameNo) {
    this.gameNo = gameNo;
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
