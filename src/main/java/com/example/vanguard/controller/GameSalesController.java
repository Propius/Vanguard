package com.example.vanguard.controller;

import com.example.vanguard.common.enumeration.FilterType;
import com.example.vanguard.common.enumeration.PeriodFilterType;
import com.example.vanguard.dto.TotalSalesProjection;
import com.example.vanguard.entity.GameSales;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Game API")
public interface GameSalesController {

  @Operation(summary = "Import CSV File")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "CSV file imported successfully",
            content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid CSV file", content = @Content),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error",
            content = @Content)
      })
  CompletableFuture<Void> importCsv(@RequestParam("file") MultipartFile file);

  @Operation(summary = "Get Game Sales")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Success", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid parameters", content = @Content),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error",
            content = @Content)
      })
  CompletableFuture<Page<GameSales>> getGameSales(
      @RequestParam(required = false) @Schema(example = "2025-04-01") LocalDate fromDate,
      @RequestParam(required = false) @Schema(example = "2025-04-30") LocalDate toDate,
      @RequestParam(required = false) @Schema(example = "1000") BigDecimal salePrice,
      @RequestParam(required = false, defaultValue = "")
          @Schema(
              implementation = FilterType.class,
              allowableValues = {"LESS_THAN", "GREATER_THAN"})
          FilterType filterType,
      @ParameterObject Pageable pageable);

  @Operation(summary = "Get Total Sales")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Success", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid parameters", content = @Content),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error",
            content = @Content)
      })
  CompletableFuture<List<TotalSalesProjection>> getTotalSales(
      @RequestParam
          @Schema(
              implementation = PeriodFilterType.class,
              allowableValues = {"HOURLY", "DAILY", "WEEKLY", "MONTHLY", "QUARTERLY", "YEARLY"})
          PeriodFilterType period,
      @RequestParam(required = false) Integer gameNo);
}
