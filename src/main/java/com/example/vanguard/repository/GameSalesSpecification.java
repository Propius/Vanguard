package com.example.vanguard.repository;

import com.example.vanguard.common.enumeration.FilterType;
import com.example.vanguard.entity.GameSales;
import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.data.jpa.domain.Specification;

public class GameSalesSpecification {
  public GameSalesSpecification() {
    // Default constructor
  }

  public static Specification<GameSales> buildSpecification(
      ZonedDateTime fromDate, ZonedDateTime toDate, BigDecimal salePrice, FilterType filterType) {
    return ((root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (fromDate != null) {
        predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dateOfSale"), fromDate));
      }
      if (toDate != null) {
        predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dateOfSale"), toDate));
      }
      if (salePrice != null && Objects.equals(FilterType.LESS_THAN, filterType)) {
        predicates.add(criteriaBuilder.lessThan(root.get("salePrice"), salePrice));
      }
      if (salePrice != null && Objects.equals(FilterType.GREATER_THAN, filterType)) {
        predicates.add(criteriaBuilder.greaterThan(root.get("salePrice"), salePrice));
      }

      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    });
  }
}
