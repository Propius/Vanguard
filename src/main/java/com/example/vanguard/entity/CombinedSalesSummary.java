package com.example.vanguard.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

@Entity
@Immutable
@Subselect("SELECT * FROM combined_sales_summary_view")
public class CombinedSalesSummary implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @Column(nullable = false)
  private Long id;

  @Column(name = "period_of_sale", nullable = false)
  private String periodOfSale;

  @Column(name = "game_no")
  private Integer gameNo;

  @Column(name = "total_games_sold", nullable = false)
  private Integer totalGamesSold;

  @Column(name = "total_sales", nullable = false)
  private BigDecimal totalSales;

  @Column(name = "type", nullable = false)
  private String type;

  public CombinedSalesSummary() {
    // No-args Constructor
  }

  public Long getId() {
    return id;
  }

  public String getPeriodOfSale() {
    return periodOfSale;
  }

  public Integer getGameNo() {
    return gameNo;
  }

  public Integer getTotalGamesSold() {
    return totalGamesSold;
  }

  public BigDecimal getTotalSales() {
    return totalSales;
  }

  public String getType() {
    return type;
  }
}
