package com.example.vanguard.dto;

import java.math.BigDecimal;

public record TotalSalesDto(
    String periodOfSale, Integer gameNo, Integer totalGamesSold, BigDecimal totalSales) {}
