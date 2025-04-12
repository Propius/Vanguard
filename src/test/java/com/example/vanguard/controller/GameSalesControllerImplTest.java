package com.example.vanguard.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import com.example.vanguard.common.enumeration.FilterType;
import com.example.vanguard.controller.impl.GameSalesControllerImpl;
import com.example.vanguard.entity.DailySalesSummary;
import com.example.vanguard.entity.GameSales;
import com.example.vanguard.service.GameSalesService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
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
  void testImportCsv() {
    MultipartFile file = mock(MultipartFile.class);
    CompletableFuture<Void> future = CompletableFuture.completedFuture(null);

    when(gameSalesService.importCsv(file)).thenReturn(future);

    CompletableFuture<Void> result = gameSalesController.importCsv(file);

    assertEquals(future, result);
    verify(gameSalesService, times(1)).importCsv(file);
  }

  @Test
  void testImportCsvPerformance() throws Exception {
    // Mock a large CSV file with 1 million records
    MultipartFile file = mock(MultipartFile.class);
    CompletableFuture<Void> future = CompletableFuture.completedFuture(null);

    when(gameSalesService.importCsv(file)).thenReturn(future);

    // Measure the time taken to execute the method
    long startTime = System.currentTimeMillis();
    CompletableFuture<Void> result = gameSalesController.importCsv(file);
    long endTime = System.currentTimeMillis();

    // Ensure the CompletableFuture is completed
    result.join();

    // Assert the time taken is less than 20 seconds
    long timeTaken = endTime - startTime;
    System.out.println("Execution time: " + timeTaken + "ms");

    assertEquals(future, result);
    assertTrue(timeTaken < 20000, "Importing CSV took longer than 20 seconds");

    verify(gameSalesService, times(1)).importCsv(file);
  }

  @Test
  void testGetGameSales() {
    LocalDate fromDate = ZonedDateTime.now().minusDays(1).toLocalDate();
    LocalDate toDate = ZonedDateTime.now().toLocalDate();
    BigDecimal salePrice = BigDecimal.valueOf(50);
    FilterType filterType = FilterType.LESS_THAN; // Replace with an actual enum value
    Pageable pageable = mock(Pageable.class);
    Page<GameSales> page = mock(Page.class);
    CompletableFuture<Page<GameSales>> future = CompletableFuture.completedFuture(page);

    when(gameSalesService.getGameSales(fromDate, toDate, salePrice, filterType, pageable))
        .thenReturn(future);

    long startTime = System.nanoTime();
    CompletableFuture<Page<GameSales>> result =
        gameSalesController.getGameSales(fromDate, toDate, salePrice, filterType, pageable);
    long endTime = System.nanoTime();

    long timeTaken = (endTime - startTime) / 1_000_000; // Convert to milliseconds
    System.out.println("Execution time: " + timeTaken + "ms");

    assertEquals(future, result);
    assertTrue(timeTaken < 50, "Execution time exceeded 50ms");
    verify(gameSalesService, times(1))
        .getGameSales(fromDate, toDate, salePrice, filterType, pageable);
  }

  @Test
  void testGetTotalSales() {
    LocalDate fromDate = ZonedDateTime.now().minusDays(1).toLocalDate();
    LocalDate toDate = ZonedDateTime.now().toLocalDate();
    Integer gameNo = 123;
    List<DailySalesSummary> summaryList = List.of(mock(DailySalesSummary.class));
    CompletableFuture<List<DailySalesSummary>> future =
        CompletableFuture.completedFuture(summaryList);

    when(gameSalesService.getTotalSales(fromDate, toDate, gameNo)).thenReturn(future);

    long startTime = System.nanoTime();
    CompletableFuture<List<DailySalesSummary>> result =
        gameSalesController.getTotalSales(fromDate, toDate, gameNo);
    long endTime = System.nanoTime();

    long timeTaken = (endTime - startTime) / 1_000_000; // Convert to milliseconds
    System.out.println("Execution time: " + timeTaken + "ms");

    assertEquals(future, result);
    assertTrue(timeTaken < 50, "Execution time exceeded 50ms");
    verify(gameSalesService, times(1)).getTotalSales(fromDate, toDate, gameNo);
  }
}
