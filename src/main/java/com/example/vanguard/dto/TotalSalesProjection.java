package com.example.vanguard.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public record TotalSalesProjection(
    String periodOfSale, Integer gameNo, Integer totalGamesSold, BigDecimal totalSales)
    implements Serializable {}
