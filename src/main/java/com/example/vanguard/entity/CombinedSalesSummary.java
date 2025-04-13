package com.example.vanguard.entity;

import com.example.vanguard.common.enumeration.PeriodFilterType;
import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

@Entity
@Immutable
@Subselect("SELECT * FROM combined_sales_summary_view")
@IdClass(CombinedSalesSummaryId.class)
public class CombinedSalesSummary implements Serializable {

  @Id
  @Column(name = "period_of_sale", nullable = false)
  private String periodOfSale;

  @Id
  @Column(name = "game_no", nullable = false)
  private Integer gameNo;

  @Column(name = "total_games_sold", nullable = false)
  private int totalGamesSold;

  @Column(name = "total_sales", nullable = false)
  private BigDecimal totalSales;

  @Column(name = "type", nullable = false)
  private PeriodFilterType type;

  public CombinedSalesSummary() {
    // No-args Constructor
  }

  public String getPeriodOfSale() {
    return periodOfSale;
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

  public PeriodFilterType getType() {
    return type;
  }
}
