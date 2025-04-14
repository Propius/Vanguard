package com.example.vanguard.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.vanguard.common.enumeration.FilterType;
import com.example.vanguard.common.enumeration.PeriodFilterType;
import com.example.vanguard.controller.impl.GameSalesControllerImpl;
import com.example.vanguard.dto.TotalSalesProjection;
import com.example.vanguard.entity.GameSales;
import com.example.vanguard.service.GameSalesService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

class GameSalesControllerImplTest {

  @Mock private GameSalesService gameSalesService;

  @InjectMocks private GameSalesControllerImpl gameSalesController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testImportCsv_Success() {
    MultipartFile file = mock(MultipartFile.class);
    CompletableFuture<Void> future = CompletableFuture.completedFuture(null);

    when(gameSalesService.importCsv(file)).thenReturn(future);

    CompletableFuture<Void> result = gameSalesController.importCsv(file);

    assertEquals(future, result);
    verify(gameSalesService, times(1)).importCsv(file);
  }

  @Test
  void testGetGameSales_Success() {
    LocalDate fromDate = LocalDate.now().minusDays(1);
    LocalDate toDate = LocalDate.now();
    BigDecimal salePrice = BigDecimal.valueOf(50);
    FilterType filterType = FilterType.LESS_THAN;
    Pageable pageable = mock(Pageable.class);
    Page<GameSales> page = mock(Page.class);
    CompletableFuture<Page<GameSales>> future = CompletableFuture.completedFuture(page);

    when(gameSalesService.getGameSales(fromDate, toDate, salePrice, filterType, pageable))
        .thenReturn(future);

    CompletableFuture<Page<GameSales>> result =
        gameSalesController.getGameSales(fromDate, toDate, salePrice, filterType, pageable);

    assertEquals(future, result);
    verify(gameSalesService, times(1))
        .getGameSales(fromDate, toDate, salePrice, filterType, pageable);
  }

  @Test
  void testGetGameSales_NullParameters() {
    Pageable pageable = mock(Pageable.class);
    Page<GameSales> page = mock(Page.class);
    CompletableFuture<Page<GameSales>> future = CompletableFuture.completedFuture(page);

    when(gameSalesService.getGameSales(null, null, null, null, pageable)).thenReturn(future);

    CompletableFuture<Page<GameSales>> result =
        gameSalesController.getGameSales(null, null, null, null, pageable);

    assertEquals(future, result);
    verify(gameSalesService, times(1)).getGameSales(null, null, null, null, pageable);
  }

  @Test
  void testGetTotalSales_Success() {
    PeriodFilterType period = PeriodFilterType.MONTHLY;
    Integer gameNo = 123;
    List<TotalSalesProjection> summaryList = List.of(mock(TotalSalesProjection.class));
    CompletableFuture<List<TotalSalesProjection>> future =
        CompletableFuture.completedFuture(summaryList);

    when(gameSalesService.getTotalSales(period, gameNo)).thenReturn(future);

    CompletableFuture<List<TotalSalesProjection>> result =
        gameSalesController.getTotalSales(period, gameNo);

    assertEquals(future, result);
    verify(gameSalesService, times(1)).getTotalSales(period, gameNo);
  }

  @Test
  void testGetTotalSales_NullParameters() {
    List<TotalSalesProjection> summaryList = List.of(mock(TotalSalesProjection.class));
    CompletableFuture<List<TotalSalesProjection>> future =
        CompletableFuture.completedFuture(summaryList);

    when(gameSalesService.getTotalSales(null, null)).thenReturn(future);

    CompletableFuture<List<TotalSalesProjection>> result =
        gameSalesController.getTotalSales(null, null);

    assertEquals(future, result);
    verify(gameSalesService, times(1)).getTotalSales(null, null);
  }
}
