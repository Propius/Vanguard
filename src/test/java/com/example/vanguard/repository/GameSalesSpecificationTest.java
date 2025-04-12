package com.example.vanguard.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import com.example.vanguard.common.enumeration.FilterType;
import com.example.vanguard.entity.GameSales;
import jakarta.persistence.criteria.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

class GameSalesSpecificationTest {

  @Test
  void testBuildSpecificationWithAllParameters() {
    LocalDate fromDate = LocalDate.now().minusDays(5);
    LocalDate toDate = LocalDate.now();
    BigDecimal salePrice = BigDecimal.valueOf(100);
    FilterType filterType = FilterType.LESS_THAN;

    Specification<GameSales> specification =
        GameSalesSpecification.buildSpecification(fromDate, toDate, salePrice, filterType);

    assertNotNull(specification);

    // Mocking the JPA Criteria API components
    Root<GameSales> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);

    // Mocking the behavior of CriteriaBuilder
    Predicate mockPredicate = mock(Predicate.class);
    when(criteriaBuilder.and(any(Predicate[].class))).thenReturn(mockPredicate);
    when(criteriaBuilder.greaterThanOrEqualTo(any(), eq(fromDate))).thenReturn(mockPredicate);
    when(criteriaBuilder.lessThanOrEqualTo(any(), eq(toDate))).thenReturn(mockPredicate);
    when(criteriaBuilder.lessThan(any(), eq(salePrice))).thenReturn(mockPredicate);
    when(criteriaBuilder.greaterThan(any(), eq(salePrice))).thenReturn(mockPredicate);

    // Mocking the behavior of Root
    when(root.get("dateOfSale")).thenReturn(mock(Path.class));
    when(root.get("salePrice")).thenReturn(mock(Path.class));

    Predicate predicate = specification.toPredicate(root, query, criteriaBuilder);
    assertNotNull(predicate, "The toPredicate method returned null. Check the implementation.");
  }

  @Test
  void testBuildSpecificationWithFromDateOnly() {
    LocalDate fromDate = LocalDate.now().minusDays(5);

    Specification<GameSales> specification =
        GameSalesSpecification.buildSpecification(fromDate, null, null, null);

    assertNotNull(specification);

    Root<GameSales> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);

    Predicate mockPredicate = mock(Predicate.class);
    when(criteriaBuilder.and(any(Predicate[].class))).thenReturn(mockPredicate);
    when(criteriaBuilder.greaterThanOrEqualTo(any(), eq(fromDate))).thenReturn(mockPredicate);

    when(root.get("dateOfSale")).thenReturn(mock(Path.class));

    Predicate predicate = specification.toPredicate(root, query, criteriaBuilder);
    assertNotNull(predicate);
  }

  @Test
  void testBuildSpecificationWithToDateOnly() {
    LocalDate toDate = LocalDate.now();

    Specification<GameSales> specification =
        GameSalesSpecification.buildSpecification(null, toDate, null, null);

    assertNotNull(specification);

    Root<GameSales> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);

    Predicate mockPredicate = mock(Predicate.class);
    when(criteriaBuilder.and(any(Predicate[].class))).thenReturn(mockPredicate);
    when(criteriaBuilder.lessThanOrEqualTo(any(), eq(toDate))).thenReturn(mockPredicate);

    when(root.get("dateOfSale")).thenReturn(mock(Path.class));

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

    Predicate mockPredicate = mock(Predicate.class);
    when(criteriaBuilder.and(any(Predicate[].class))).thenReturn(mockPredicate);
    when(criteriaBuilder.lessThan(any(), eq(salePrice))).thenReturn(mockPredicate);

    when(root.get("salePrice")).thenReturn(mock(Path.class));

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

    Predicate mockPredicate = mock(Predicate.class);
    when(criteriaBuilder.and(any(Predicate[].class))).thenReturn(mockPredicate);
    when(criteriaBuilder.greaterThan(any(), eq(salePrice))).thenReturn(mockPredicate);

    when(root.get("salePrice")).thenReturn(mock(Path.class));

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

    Predicate mockPredicate = mock(Predicate.class);
    when(criteriaBuilder.and(any(Predicate[].class))).thenReturn(mockPredicate);

    Predicate predicate = specification.toPredicate(root, query, criteriaBuilder);
    assertNotNull(predicate);
  }
}
