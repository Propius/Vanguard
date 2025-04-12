package com.example.vanguard.service.impl;

import com.example.vanguard.common.enumeration.FilterType;
import com.example.vanguard.entity.DailySalesSummary;
import com.example.vanguard.entity.GameSales;
import com.example.vanguard.repository.DailySalesSummaryRepository;
import com.example.vanguard.repository.GameSalesRepository;
import com.example.vanguard.repository.GameSalesSpecification;
import com.example.vanguard.service.GameSalesService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class GameSalesServiceImpl implements GameSalesService {

  private final GameSalesRepository gameSalesRepository;
  private final DailySalesSummaryRepository dailySalesSummaryRepository;

  public GameSalesServiceImpl(
      GameSalesRepository gameSalesRepository,
      DailySalesSummaryRepository dailySalesSummaryRepository) {
    this.gameSalesRepository = gameSalesRepository;
    this.dailySalesSummaryRepository = dailySalesSummaryRepository;
  }

  /**
   * Imports a CSV file containing game sales data.
   *
   * @param file the CSV file to import
   * @return a CompletableFuture that completes when the import is finished
   */
  @Override
  public CompletableFuture<Void> importCsv(MultipartFile file) {
    return CompletableFuture.runAsync(
        () -> {
          long startTime = System.currentTimeMillis();
          try (BufferedReader reader =
              new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            ExecutorService executor =
                Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            List<String> batch = new ArrayList<>();
            int chunkSize = 10000;

            String line;
            reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
              batch.add(line);
              if (batch.size() == chunkSize) {
                List<String> chunk = new ArrayList<>(batch);
                futures.add(CompletableFuture.runAsync(() -> processChunk(chunk), executor));
                batch.clear();
              }
            }
            if (!batch.isEmpty()) {
              List<String> chunk = new ArrayList<>(batch);
              futures.add(CompletableFuture.runAsync(() -> processChunk(chunk), executor));
            }

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            executor.shutdown();
          } catch (IOException e) {
            throw new RuntimeException("Failed to process CSV file: " + e.getMessage(), e);
          }
          long endTime = System.currentTimeMillis();
          System.out.println("CSV import completed in " + (endTime - startTime) + "ms");
        });
  }

  /**
   * Retrieves a paginated list of game sales filtered by date and price.
   *
   * @param fromDate the start date for filtering
   * @param toDate the end date for filtering
   * @param salePrice the sale price for filtering
   * @param filterType the filter type (e.g., "greater than", "less than")
   * @param pageable pagination information
   * @return a CompletableFuture containing a page of game sales
   */
  @Override
  public CompletableFuture<Page<GameSales>> getGameSales(
      LocalDate fromDate,
      LocalDate toDate,
      BigDecimal salePrice,
      FilterType filterType,
      Pageable pageable) {

    if (pageable.getPageSize() > 100) {
      throw new IllegalArgumentException("Page size must not exceed 100 records per request");
    }

    if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
      throw new IllegalArgumentException("fromDate must not be after toDate");
    }

    if (salePrice != null && salePrice.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("salePrice must not be negative");
    }

    return CompletableFuture.supplyAsync(
        () -> {
          Specification<GameSales> specification =
              GameSalesSpecification.buildSpecification(fromDate, toDate, salePrice, filterType);
          return gameSalesRepository.findAll(specification, pageable);
        });
  }

  /**
   * Retrieves a paginated list of game sales filtered by date.
   *
   * @param fromDate the start date for filtering
   * @param toDate the end date for filtering
   * @param gameNo the game number for filtering
   * @return a CompletableFuture containing a page of game sales
   */
  @Override
  @Cacheable(value = "totalSales", key = "#fromDate + '-' + #toDate + '-' + #gameNo")
  public CompletableFuture<List<DailySalesSummary>> getTotalSales(
      LocalDate fromDate, LocalDate toDate, Integer gameNo) {
    return CompletableFuture.supplyAsync(
        () -> dailySalesSummaryRepository.findAggregatedSales(fromDate, toDate, gameNo));
  }

  private void processChunk(List<String> chunk) {
    List<GameSales> batch = new ArrayList<>();
    for (String line : chunk) {
      String[] columns = line.split(",");
      if (columns.length != 9) {
        throw new IllegalArgumentException("Invalid CSV format: Each row must have 9 columns");
      }
      try {
        GameSales gameSales =
            GameSales.builder()
                .gameNo(Integer.parseInt(columns[1]))
                .gameName(columns[2])
                .gameCode(columns[3])
                .type(Integer.parseInt(columns[4]))
                .costPrice(new BigDecimal(columns[5]))
                .tax(new BigDecimal(columns[6]))
                .salePrice(new BigDecimal(columns[7]))
                .dateOfSale(ZonedDateTime.parse(columns[8]))
                .build();
        batch.add(gameSales);
      } catch (NumberFormatException | DateTimeParseException e) {
        throw new IllegalArgumentException("Invalid data format in CSV: " + e.getMessage(), e);
      }
    }
    gameSalesRepository.saveAll(batch);
  }
}
