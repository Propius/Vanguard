package com.example.vanguard.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.vanguard.common.enumeration.FilterType;
import com.example.vanguard.entity.DailySalesSummary;
import com.example.vanguard.entity.GameSales;
import com.example.vanguard.repository.DailySalesSummaryRepository;
import com.example.vanguard.repository.GameSalesRepository;
import com.example.vanguard.service.impl.GameSalesServiceImpl;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

class GameSalesServiceImplTest {

  @Mock private GameSalesRepository gameSalesRepository;

  @Mock private DailySalesSummaryRepository dailySalesSummaryRepository;

  @InjectMocks private GameSalesServiceImpl gameSalesService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testImportCsv() throws Exception {
    String csvContent = "header\n1,123,GameName,Code,1,10.0,1.0,11.0,2023-01-01T00:00:00Z\n";
    InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());
    MultipartFile file = mock(MultipartFile.class);

    when(file.getInputStream()).thenReturn(inputStream);
    when(gameSalesRepository.existsByGameNoAndDateOfSale(
            123, ZonedDateTime.parse("2023-01-01T00:00:00Z")))
        .thenReturn(false);

    gameSalesService.importCsv(file).join();

    verify(gameSalesRepository, times(1)).saveAll(anyList());
  }

  @Test
  void testImportCsvWithInvalidData() throws Exception {
    String csvContent = "header\n1,invalid,GameName,Code,1,10.0,1.0,11.0,invalid-date\n";
    InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());
    MultipartFile file = mock(MultipartFile.class);

    when(file.getInputStream()).thenReturn(inputStream);

    CompletionException exception =
        assertThrows(CompletionException.class, () -> gameSalesService.importCsv(file).join());

    assertInstanceOf(IllegalArgumentException.class, exception.getCause());
  }

  @Test
  void testGetGameSales() {
    LocalDate fromDate = ZonedDateTime.now().minusDays(1).toLocalDate();
    LocalDate toDate = ZonedDateTime.now().toLocalDate();
    BigDecimal salePrice = BigDecimal.valueOf(50);
    FilterType filterType = FilterType.LESS_THAN;
    Pageable pageable = mock(Pageable.class);
    Page<GameSales> page = mock(Page.class);

    when(pageable.getPageSize()).thenReturn(50);
    when(gameSalesRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

    CompletableFuture<Page<GameSales>> result =
        gameSalesService.getGameSales(fromDate, toDate, salePrice, filterType, pageable);

    assertEquals(page, result.join());
    verify(gameSalesRepository, times(1)).findAll(any(Specification.class), eq(pageable));
  }

  @Test
  void testGetGameSalesWithInvalidPageSize() {
    Pageable pageable = mock(Pageable.class);
    when(pageable.getPageSize()).thenReturn(200);

    assertThrows(
        IllegalArgumentException.class,
        () -> gameSalesService.getGameSales(null, null, null, null, pageable).join());
  }

  @Test
  void testGetGameSalesWithInvalidDateRange() {
    LocalDate fromDate = ZonedDateTime.now().toLocalDate();
    LocalDate toDate = ZonedDateTime.now().minusDays(1).toLocalDate();
    Pageable pageable = mock(Pageable.class);

    when(pageable.getPageSize()).thenReturn(50);

    assertThrows(
        IllegalArgumentException.class,
        () -> gameSalesService.getGameSales(fromDate, toDate, null, null, pageable).join());
  }

  @Test
  void testGetTotalSales() {
    LocalDate fromDate = ZonedDateTime.now().minusDays(1).toLocalDate();
    LocalDate toDate = ZonedDateTime.now().toLocalDate();
    Integer gameNo = 123;
    List<DailySalesSummary> summaryList = List.of(mock(DailySalesSummary.class));

    when(dailySalesSummaryRepository.findAggregatedSales(fromDate, toDate, gameNo))
        .thenReturn(summaryList);

    CompletableFuture<List<DailySalesSummary>> result =
        gameSalesService.getTotalSales(fromDate, toDate, gameNo);

    assertEquals(summaryList, result.join());
    verify(dailySalesSummaryRepository, times(1)).findAggregatedSales(fromDate, toDate, gameNo);
  }
}
