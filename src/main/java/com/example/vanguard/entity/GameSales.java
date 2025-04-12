package com.example.vanguard.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Table(
    name = "game_sales",
    indexes = {
      @Index(name = "idx_date_of_sale", columnList = "date_of_sale"),
      @Index(name = "idx_game_no", columnList = "game_no"),
      @Index(name = "idx_sale_price", columnList = "sale_price"),
      @Index(name = "idx_game_no_date_of_sale", columnList = "game_no, date_of_sale")
    })
public class GameSales implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "game_no", nullable = false)
  private Integer gameNo;

  @Column(name = "game_name", nullable = false)
  private String gameName;

  @Column(name = "game_code", nullable = false)
  private String gameCode;

  @Column(name = "type", nullable = false)
  private Integer type;

  @Column(name = "cost_price", nullable = false)
  private BigDecimal costPrice;

  @Column(name = "tax", nullable = false)
  private BigDecimal tax;

  @Column(name = "sale_price", nullable = false)
  private BigDecimal salePrice;

  @Column(name = "date_of_sale", nullable = false)
  private ZonedDateTime dateOfSale;

  public GameSales() {
    // No-args Constructor
  }

  private GameSales(GameSalesBuilder builder) {
    this.gameNo = builder.gameNo;
    this.gameName = builder.gameName;
    this.gameCode = builder.gameCode;
    this.type = builder.type;
    this.costPrice = builder.costPrice;
    this.tax = builder.tax;
    this.salePrice = builder.salePrice;
    this.dateOfSale = builder.dateOfSale;
  }

  public static GameSalesBuilder builder() {
    return new GameSalesBuilder();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Integer getGameNo() {
    return gameNo;
  }

  public void setGameNo(Integer gameNo) {
    this.gameNo = gameNo;
  }

  public String getGameName() {
    return gameName;
  }

  public void setGameName(String gameName) {
    this.gameName = gameName;
  }

  public String getGameCode() {
    return gameCode;
  }

  public void setGameCode(String gameCode) {
    this.gameCode = gameCode;
  }

  public Integer getType() {
    return type;
  }

  public void setType(Integer type) {
    this.type = type;
  }

  public BigDecimal getCostPrice() {
    return costPrice;
  }

  public void setCostPrice(BigDecimal costPrice) {
    this.costPrice = costPrice;
  }

  public BigDecimal getTax() {
    return tax;
  }

  public void setTax(BigDecimal tax) {
    this.tax = tax;
  }

  public BigDecimal getSalePrice() {
    return salePrice;
  }

  public void setSalePrice(BigDecimal salePrice) {
    this.salePrice = salePrice;
  }

  public ZonedDateTime getDateOfSale() {
    return dateOfSale;
  }

  public void setDateOfSale(ZonedDateTime dateOfSale) {
    this.dateOfSale = dateOfSale;
  }

  public static final class GameSalesBuilder {

    private Integer gameNo;
    private String gameName;
    private String gameCode;
    private Integer type;
    private BigDecimal costPrice;
    private BigDecimal tax;
    private BigDecimal salePrice;
    private ZonedDateTime dateOfSale;

    private GameSalesBuilder() {}

    public GameSalesBuilder gameNo(Integer gameNo) {
      this.gameNo = gameNo;
      return this;
    }

    public GameSalesBuilder gameName(String gameName) {
      this.gameName = gameName;
      return this;
    }

    public GameSalesBuilder gameCode(String gameCode) {
      this.gameCode = gameCode;
      return this;
    }

    public GameSalesBuilder type(Integer type) {
      this.type = type;
      return this;
    }

    public GameSalesBuilder costPrice(BigDecimal costPrice) {
      this.costPrice = costPrice;
      return this;
    }

    public GameSalesBuilder tax(BigDecimal tax) {
      this.tax = tax;
      return this;
    }

    public GameSalesBuilder salePrice(BigDecimal salePrice) {
      this.salePrice = salePrice;
      return this;
    }

    public GameSalesBuilder dateOfSale(ZonedDateTime dateOfSale) {
      this.dateOfSale = dateOfSale;
      return this;
    }

    public GameSales build() {
      return new GameSales(this);
    }
  }
}
