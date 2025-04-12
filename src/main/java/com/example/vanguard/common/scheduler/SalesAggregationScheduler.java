package com.example.vanguard.common.scheduler;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SalesAggregationScheduler {

  private final JdbcTemplate jdbcTemplate;

  public SalesAggregationScheduler(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Scheduled(fixedRate = 3600000) // Run every hour
  public void aggregateSalesData() {
    String query =
        "INSERT INTO daily_sales_summary (date_of_sale, game_no, total_games_sold, total_sales) "
            + "SELECT DATE(date_of_sale), game_no, COUNT(*), SUM(sale_price) "
            + "FROM game_sales "
            + "GROUP BY DATE(date_of_sale), game_no "
            + "ON DUPLICATE KEY UPDATE "
            + "total_games_sold = VALUES(total_games_sold), total_sales = VALUES(total_sales)";
    jdbcTemplate.execute(query);
  }
}
