package com.example.vanguard.controller.impl;

import com.example.vanguard.common.enumeration.FilterType;
import com.example.vanguard.common.enumeration.PeriodFilterType;
import com.example.vanguard.controller.GameSalesController;
import com.example.vanguard.dto.TotalSalesProjection;
import com.example.vanguard.entity.GameSales;
import com.example.vanguard.service.GameSalesService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/game-sales")
public class GameSalesControllerImpl implements GameSalesController {

  private final GameSalesService gameSalesService;

  public GameSalesControllerImpl(GameSalesService gameSalesService) {
    this.gameSalesService = gameSalesService;
  }

  @Override
  @PostMapping(consumes = "multipart/form-data")
  @ResponseStatus(HttpStatus.CREATED)
  public CompletableFuture<Void> importCsv(@RequestParam("file") MultipartFile file) {
    return gameSalesService.importCsv(file);
  }

  @Override
  @GetMapping("/getGameSales")
  @ResponseStatus(HttpStatus.OK)
  public CompletableFuture<Page<GameSales>> getGameSales(
      @RequestParam(required = false) LocalDate fromDate,
      @RequestParam(required = false) LocalDate toDate,
      @RequestParam(required = false) BigDecimal salePrice,
      @RequestParam(required = false, defaultValue = "") FilterType filterType,
      @PageableDefault(sort = "gameNo", direction = Sort.Direction.ASC, size = 100)
          Pageable pageable) {
    return gameSalesService.getGameSales(fromDate, toDate, salePrice, filterType, pageable);
  }

  @Override
  @GetMapping("/getTotalSales")
  @ResponseStatus(HttpStatus.OK)
  public CompletableFuture<List<TotalSalesProjection>> getTotalSales(
      @RequestParam PeriodFilterType period, @RequestParam(required = false) Integer gameNo) {
    return gameSalesService.getTotalSales(period, gameNo);
  }
}
