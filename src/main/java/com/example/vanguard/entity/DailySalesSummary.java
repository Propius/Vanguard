package com.example.vanguard.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

@Entity
@Immutable
@Subselect("SELECT * FROM daily_sales_summary_view")
@IdClass(DailySalesSummaryId.class)
public class DailySalesSummary implements Serializable {

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

  public LocalDate getDateOfSale() {
    return dateOfSale;
  }

  public Integer getGameNo() {
    return gameNo;
  }

  public int getTotalGamesSold() {
    return totalGamesSold;
  }

  public BigDecimal getTotalSales() {
    return totalSales;
  }
}
