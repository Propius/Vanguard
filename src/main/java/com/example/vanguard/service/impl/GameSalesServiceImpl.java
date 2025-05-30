package com.example.vanguard.service.impl;

import com.example.vanguard.common.config.RabbitMQConfig;
import com.example.vanguard.common.enumeration.FilterType;
import com.example.vanguard.common.enumeration.PeriodFilterType;
import com.example.vanguard.dto.TotalSalesProjection;
import com.example.vanguard.entity.GameSales;
import com.example.vanguard.exception.CsvProcessingException;
import com.example.vanguard.repository.CombinedSalesSummaryRepository;
import com.example.vanguard.repository.GameSalesRepository;
import com.example.vanguard.repository.GameSalesSpecification;
import com.example.vanguard.service.GameSalesService;
import com.example.vanguard.util.ValidationUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.Cache;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class GameSalesServiceImpl implements GameSalesService {

  private static final Logger log = LoggerFactory.getLogger(GameSalesServiceImpl.class);

  private final GameSalesRepository gameSalesRepository;
  private final CombinedSalesSummaryRepository combinedSalesSummaryRepository;
  private final RabbitTemplate rabbitTemplate;
  private final JCacheCacheManager cacheManager;

  public GameSalesServiceImpl(
      GameSalesRepository gameSalesRepository,
      CombinedSalesSummaryRepository combinedSalesSummaryRepository,
      RabbitTemplate rabbitTemplate,
      JCacheCacheManager cacheManager) {
    this.gameSalesRepository = gameSalesRepository;
    this.combinedSalesSummaryRepository = combinedSalesSummaryRepository;
    this.rabbitTemplate = rabbitTemplate;
    this.cacheManager = cacheManager;
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
            String line;
            reader.readLine(); // Skip header
            int id = 1; // Start ID from 1
            List<String> chunk = new ArrayList<>();
            int chunkSize = 50000; // Size of each chunk

            while ((line = reader.readLine()) != null) {
              String[] columns = line.split(",");
              if (columns.length != 9) {
                throw new IllegalArgumentException(
                    "Invalid CSV format: Each row must have 9 columns");
              }

              // Validate and process each column
              Integer gameNo =
                  ValidationUtils.validateRange(Integer.parseInt(columns[1]), 1, 100, "Game No");
              String gameName = ValidationUtils.validateStringField(columns[2], "Game Name", 20);
              String gameCode = ValidationUtils.validateStringField(columns[3], "Game Code", 5);
              Integer type = ValidationUtils.validateType(columns[4]);
              BigDecimal costPrice =
                  ValidationUtils.validateBigDecimal(
                      columns[5], "Cost Price", new BigDecimal("100"));
              BigDecimal tax =
                  ValidationUtils.validateBigDecimal(columns[6], "Tax", new BigDecimal("100"));
              BigDecimal salePrice =
                  ValidationUtils.validateBigDecimal(columns[7], "Tax", new BigDecimal("100"));
              ZonedDateTime dateOfSale = ValidationUtils.validateDate(columns[8]);

              // Construct the CSV line with ID
              String processedLine =
                  String.join(
                      ",",
                      String.valueOf(id++),
                      String.valueOf(gameNo),
                      gameName,
                      gameCode,
                      String.valueOf(type),
                      costPrice.toString(),
                      tax.toString(),
                      salePrice.toString(),
                      dateOfSale.toString());

              chunk.add(processedLine);

              // If chunk size is reached, send to RabbitMQ
              if (chunk.size() == chunkSize) {
                executeRabbitMq(chunk);
              }
            }

            // Send any remaining lines in the last chunk
            if (!chunk.isEmpty()) {
              executeRabbitMq(chunk);
            }
          } catch (IOException e) {
            throw new CsvProcessingException("Failed to process CSV file: " + e.getMessage(), e);
          }
          long endTime = System.currentTimeMillis();
          // Print the time taken
          log.info("Time taken to import CSV: {} ms", (endTime - startTime));
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

    // Measure start time
    long startTime = System.currentTimeMillis();

    ValidationUtils.validatePageSize(pageable.getPageSize());
    ValidationUtils.validateDateRange(fromDate, toDate);
    ValidationUtils.validateSalePrice(salePrice);

    return CompletableFuture.supplyAsync(
        () -> {
          Cache cache = cacheManager.getCache("gameSales");
          String cacheKey =
              fromDate
                  + "-"
                  + toDate
                  + "-"
                  + salePrice
                  + "-"
                  + filterType
                  + "-"
                  + pageable.getPageNumber();

          // Try to retrieve the cached value
          Page<GameSales> cachedResult = cache != null ? cache.get(cacheKey, Page.class) : null;
          if (cachedResult != null) {
            // Measure end time
            long endTime = System.currentTimeMillis();

            // Print the time taken
            log.info("Time taken to get game sales: {} ms", (endTime - startTime));

            return cachedResult;
          }

          Specification<GameSales> specification =
              GameSalesSpecification.buildSpecification(fromDate, toDate, salePrice, filterType);

          Page<GameSales> gameSalesPage = gameSalesRepository.findAll(specification, pageable);

          // Store the result in the cache
          if (cache != null) {
            cache.put(cacheKey, gameSalesPage);
          }

          // Measure end time
          long endTime = System.currentTimeMillis();

          // Print the time taken
          log.info("Time taken to get game sales: {} ms", (endTime - startTime));
          return gameSalesPage;
        });
  }

  /**
   * Retrieves the total sales summary for a specific period and game number.
   *
   * @param period the period filter type (e.g., daily, weekly, monthly)
   * @param gameNo the game number to filter by
   * @return a CompletableFuture containing a list of total sales projections
   */
  @Override
  public CompletableFuture<List<TotalSalesProjection>> getTotalSales(
      PeriodFilterType period, Integer gameNo) {
    return CompletableFuture.supplyAsync(
        () -> {
          // Measure start time
          long startTime = System.currentTimeMillis();

          Cache cache = cacheManager.getCache("totalSales");
          String cacheKey = period.name() + "-" + gameNo;

          // Try to retrieve the cached value
          List<TotalSalesProjection> cachedResult =
              cache != null ? cache.get(cacheKey, List.class) : null;
          if (cachedResult != null) {
            // Measure end time
            long endTime = System.currentTimeMillis();

            // Print the time taken
            log.info("Time taken to get total sales: {} ms", (endTime - startTime));

            return cachedResult;
          }

          List<TotalSalesProjection> totalSales =
              (gameNo == null)
                  ? combinedSalesSummaryRepository.findByTypeAndGameNoIsNull(period.name())
                  : combinedSalesSummaryRepository.findByTypeAndGameNo(period.name(), gameNo);

          // Store the result in the cache
          if (cache != null) {
            cache.put(cacheKey, totalSales);
          }

          // Measure end time
          long endTime = System.currentTimeMillis();

          // Print the time taken
          log.info("Time taken to get total sales: {} ms", (endTime - startTime));

          return totalSales;
        });
  }

  /**
   * Sends a chunk of data to RabbitMQ.
   *
   * @param chunk the chunk of data to send
   */
  private void executeRabbitMq(List<String> chunk) {
    RetryTemplate retryTemplate = new RetryTemplate();

    // Configure retry policy
    SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
    retryPolicy.setMaxAttempts(3); // Retry up to 3 times
    retryTemplate.setRetryPolicy(retryPolicy);

    // Configure backoff policy
    FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
    backOffPolicy.setBackOffPeriod(2000); // Wait 2 seconds between retries
    retryTemplate.setBackOffPolicy(backOffPolicy);

    try {
      retryTemplate.execute(
          context -> {
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.QUEUE_NAME, String.join("\n", chunk));
            chunk.clear();
            return null;
          });
    } catch (Exception e) {
      log.error("Failed to send chunk after retries: {}", e.getMessage());
      // Handle the failure (e.g., log or alert)
    }
  }
}
