package com.example.vanguard.repository;

import com.example.vanguard.entity.GameSales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface GameSalesRepository
    extends JpaRepository<GameSales, Long>, JpaSpecificationExecutor<GameSales> {}
