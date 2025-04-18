package com.example.vanguard.repository;

import com.example.vanguard.dto.TotalSalesProjection;
import com.example.vanguard.entity.CombinedSalesSummary;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CombinedSalesSummaryRepository extends JpaRepository<CombinedSalesSummary, Long> {
  List<TotalSalesProjection> findByTypeAndGameNo(String type, Integer gameNo);

  List<TotalSalesProjection> findByTypeAndGameNoIsNull(String period);
}
