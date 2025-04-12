package com.example.vanguard.repository;

import com.example.vanguard.entity.DailySalesSummary;
import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DailySalesSummaryRepository extends JpaRepository<DailySalesSummary, Long> {

  @Query(
      "SELECT d FROM DailySalesSummary d WHERE d.dateOfSale BETWEEN :fromDate AND :toDate AND (:gameNo IS NULL OR d.gameNo = :gameNo)")
  List<DailySalesSummary> findAggregatedSales(
      @Param("fromDate") ZonedDateTime fromDate,
      @Param("toDate") ZonedDateTime toDate,
      @Param("gameNo") Integer gameNo);
}
