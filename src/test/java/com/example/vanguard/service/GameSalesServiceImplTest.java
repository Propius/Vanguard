package com.example.vanguard.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.vanguard.common.config.RabbitMQConfig;
import com.example.vanguard.common.enumeration.FilterType;
import com.example.vanguard.entity.DailySalesSummary;
import com.example.vanguard.entity.GameSales;
import com.example.vanguard.exception.CsvProcessingException;
import com.example.vanguard.repository.DailySalesSummaryRepository;
import com.example.vanguard.repository.GameSalesRepository;
import com.example.vanguard.service.impl.GameSalesServiceImpl;
import java.io.ByteArrayInputStream;
import java.io.IOException;
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
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.Cache;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

class GameSalesServiceImplTest {

  @Mock private GameSalesRepository gameSalesRepository;
  @Mock private DailySalesSummaryRepository dailySalesSummaryRepository;
  @Mock private RabbitTemplate rabbitTemplate;
  @Mock private JCacheCacheManager cacheManager;
  @Mock private Cache cache;

  @InjectMocks private GameSalesServiceImpl gameSalesService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    when(cacheManager.getCache(anyString())).thenReturn(cache);
  }

  @Test
  void testImportCsv_ValidCsv() throws Exception {
    String csvContent = "header\n1,100,GameName,Code,1,10.0,1.0,11.0,2023-01-01T00:00:00Z\n";
    InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());
    MultipartFile file = mock(MultipartFile.class);

    when(file.getInputStream()).thenReturn(inputStream);

    gameSalesService.importCsv(file).join();

    verify(rabbitTemplate, times(1))
        .convertAndSend(
            eq(RabbitMQConfig.EXCHANGE_NAME), eq(RabbitMQConfig.QUEUE_NAME), anyString());
  }

  @Test
  void testImportCsv_InvalidCsvFormat() throws Exception {
    String csvContent = "header\n1,100,GameName,Code,1,10.0,1.0,11.0\n"; // Missing columns
    InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());
    MultipartFile file = mock(MultipartFile.class);

    when(file.getInputStream()).thenReturn(inputStream);

    CompletionException exception =
        assertThrows(CompletionException.class, () -> gameSalesService.importCsv(file).join());
    assertTrue(exception.getCause() instanceof IllegalArgumentException);
  }

  @Test
  void testImportCsv_IOException() throws Exception {
    MultipartFile file = mock(MultipartFile.class);

    when(file.getInputStream()).thenThrow(new IOException("Test exception"));

    CompletionException exception =
        assertThrows(CompletionException.class, () -> gameSalesService.importCsv(file).join());
    assertTrue(exception.getCause() instanceof CsvProcessingException);
  }

  @Test
  void testGetGameSales_ValidInputs() {
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
  void testGetGameSales_InvalidPageSize() {
    Pageable pageable = mock(Pageable.class);
    when(pageable.getPageSize()).thenReturn(200);

    assertThrows(
        IllegalArgumentException.class,
        () -> gameSalesService.getGameSales(null, null, null, null, pageable).join());
  }

  @Test
  void testGetTotalSales_ValidInputs() {
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

  @Test
  void testGetTotalSales_CacheHit() {
    LocalDate fromDate = ZonedDateTime.now().minusDays(1).toLocalDate();
    LocalDate toDate = ZonedDateTime.now().toLocalDate();
    Integer gameNo = 123;
    List<DailySalesSummary> cachedSummaryList = List.of(mock(DailySalesSummary.class));

    when(cache.get(anyString(), eq(List.class))).thenReturn(cachedSummaryList);

    CompletableFuture<List<DailySalesSummary>> result =
        gameSalesService.getTotalSales(fromDate, toDate, gameNo);

    assertEquals(cachedSummaryList, result.join());
    verify(dailySalesSummaryRepository, never()).findAggregatedSales(any(), any(), any());
  }
}
