package com.example.vanguard.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.vanguard.common.enumeration.FilterType;
import com.example.vanguard.entity.GameSales;
import jakarta.persistence.criteria.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

class GameSalesSpecificationTest {

  @Test
  void testBuildSpecificationWithAllParameters() {
    LocalDate fromDate = ZonedDateTime.now().minusDays(5).toLocalDate();
    LocalDate toDate = ZonedDateTime.now().toLocalDate();
    BigDecimal salePrice = BigDecimal.valueOf(100);
    FilterType filterType = FilterType.LESS_THAN;

    Specification<GameSales> specification =
        GameSalesSpecification.buildSpecification(fromDate, toDate, salePrice, filterType);

    assertNotNull(specification);

    // Mocking the JPA Criteria API components
    Root<GameSales> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);

    // Configuring the mock behavior for CriteriaBuilder
    Predicate mockPredicate = mock(Predicate.class);
    when(criteriaBuilder.greaterThanOrEqualTo(root.get("dateOfSale"), fromDate))
        .thenReturn(mockPredicate);
    when(criteriaBuilder.lessThanOrEqualTo(root.get("dateOfSale"), toDate))
        .thenReturn(mockPredicate);
    when(criteriaBuilder.lessThan(root.get("salePrice"), salePrice)).thenReturn(mockPredicate);
    when(criteriaBuilder.greaterThan(root.get("salePrice"), salePrice)).thenReturn(mockPredicate);

    // Configuring the mock behavior for Root
    when(root.get("dateOfSale")).thenReturn(mock(Path.class));
    when(root.get("salePrice")).thenReturn(mock(Path.class));

    Predicate predicate = specification.toPredicate(root, query, criteriaBuilder);
    assertNotNull(
        predicate,
        "The toPredicate method returned null. Check the implementation of buildSpecification.");
  }

  @Test
  void testBuildSpecificationWithFromDateOnly() {
    LocalDate fromDate = ZonedDateTime.now().minusDays(5).toLocalDate();

    Specification<GameSales> specification =
        GameSalesSpecification.buildSpecification(fromDate, null, null, null);

    assertNotNull(specification);

    Root<GameSales> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);

    Predicate predicate = specification.toPredicate(root, query, criteriaBuilder);
    assertNotNull(predicate);
  }

  @Test
  void testBuildSpecificationWithToDateOnly() {
    LocalDate toDate = ZonedDateTime.now().toLocalDate();

    Specification<GameSales> specification =
        GameSalesSpecification.buildSpecification(null, toDate, null, null);

    assertNotNull(specification);

    Root<GameSales> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);

    Predicate predicate = specification.toPredicate(root, query, criteriaBuilder);
    assertNotNull(predicate);
  }

  @Test
  void testBuildSpecificationWithSalePriceAndLessThanFilter() {
    BigDecimal salePrice = BigDecimal.valueOf(100);
    FilterType filterType = FilterType.LESS_THAN;

    Specification<GameSales> specification =
        GameSalesSpecification.buildSpecification(null, null, salePrice, filterType);

    assertNotNull(specification);

    Root<GameSales> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);

    Predicate predicate = specification.toPredicate(root, query, criteriaBuilder);
    assertNotNull(predicate);
  }

  @Test
  void testBuildSpecificationWithSalePriceAndGreaterThanFilter() {
    BigDecimal salePrice = BigDecimal.valueOf(100);
    FilterType filterType = FilterType.GREATER_THAN;

    Specification<GameSales> specification =
        GameSalesSpecification.buildSpecification(null, null, salePrice, filterType);

    assertNotNull(specification);

    Root<GameSales> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);

    Predicate predicate = specification.toPredicate(root, query, criteriaBuilder);
    assertNotNull(predicate);
  }

  @Test
  void testBuildSpecificationWithNoParameters() {
    Specification<GameSales> specification =
        GameSalesSpecification.buildSpecification(null, null, null, null);

    assertNotNull(specification);

    Root<GameSales> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);

    Predicate predicate = specification.toPredicate(root, query, criteriaBuilder);
    assertNotNull(predicate);
  }
}
