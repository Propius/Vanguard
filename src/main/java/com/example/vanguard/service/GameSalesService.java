package com.example.vanguard.service;

import com.example.vanguard.common.enumeration.FilterType;
import com.example.vanguard.common.enumeration.PeriodFilterType;
import com.example.vanguard.dto.TotalSalesDto;
import com.example.vanguard.entity.GameSales;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface GameSalesService {

  /**
   * Imports a CSV file containing game sales data.
   *
   * @param file the CSV file to import
   * @return a CompletableFuture that completes when the import is finished
   */
  CompletableFuture<Void> importCsv(MultipartFile file);

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
  CompletableFuture<Page<GameSales>> getGameSales(
      LocalDate fromDate,
      LocalDate toDate,
      BigDecimal salePrice,
      FilterType filterType,
      Pageable pageable);

  /**
   * Retrieves the total sales summary for a specific period and game number.
   *
   * @param period the period filter type (e.g., daily, weekly, monthly)
   * @param gameNo the game number to filter by
   * @return a CompletableFuture containing a list of daily sales summaries
   */
  CompletableFuture<List<TotalSalesDto>> getTotalSales(PeriodFilterType period, Integer gameNo);
}
