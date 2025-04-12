package com.example.vanguard.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@IdClass(DailySalesSummaryId.class)
@Table(
    name = "daily_sales_summary",
    indexes = {
      @Index(name = "idx_date_of_sale", columnList = "date_of_sale"),
      @Index(name = "idx_game_no", columnList = "game_no")
    })
public class DailySalesSummary {

  @Id
  @Column(name = "date_of_sale", nullable = false)
  private LocalDate dateOfSale;

  @Id
  @Column(name = "game_no", nullable = false)
  private Integer gameNo;

  @Column(name = "total_games_sold", nullable = false)
  private int totalGamesSold;

  @Column(name = "total_sales", nullable = false)
  private BigDecimal totalSales;

  public DailySalesSummary() {
    // No-args Constructor
  }

  private DailySalesSummary(DailySalesSummaryBuilder builder) {
    this.dateOfSale = builder.dateOfSale;
    this.gameNo = builder.gameNo;
    this.totalGamesSold = builder.totalGamesSold;
    this.totalSales = builder.totalSales;
  }

  public static DailySalesSummaryBuilder builder() {
    return new DailySalesSummaryBuilder();
  }

  public LocalDate getDateOfSale() {
    return dateOfSale;
  }

  public void setDateOfSale(LocalDate dateOfSale) {
    this.dateOfSale = dateOfSale;
  }

  public Integer getGameNo() {
    return gameNo;
  }

  public void setGameNo(Integer gameNo) {
    this.gameNo = gameNo;
  }

  public int getTotalGamesSold() {
    return totalGamesSold;
  }

  public void setTotalGamesSold(int totalGamesSold) {
    this.totalGamesSold = totalGamesSold;
  }

  public BigDecimal getTotalSales() {
    return totalSales;
  }

  public void setTotalSales(BigDecimal totalSales) {
    this.totalSales = totalSales;
  }

  public static final class DailySalesSummaryBuilder {

    private LocalDate dateOfSale;
    private Integer gameNo;
    private int totalGamesSold;
    private BigDecimal totalSales;

    private DailySalesSummaryBuilder() {}

    public DailySalesSummaryBuilder dateOfSale(LocalDate dateOfSale) {
      this.dateOfSale = dateOfSale;
      return this;
    }

    public DailySalesSummaryBuilder gameNo(Integer gameNo) {
      this.gameNo = gameNo;
      return this;
    }

    public DailySalesSummaryBuilder totalGamesSold(int totalGamesSold) {
      this.totalGamesSold = totalGamesSold;
      return this;
    }

    public DailySalesSummaryBuilder totalSales(BigDecimal totalSales) {
      this.totalSales = totalSales;
      return this;
    }

    public DailySalesSummary build() {
      return new DailySalesSummary(this);
    }
  }
}
