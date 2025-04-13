package com.example.vanguard.util;

import com.example.vanguard.dto.TotalSalesDto;
import com.example.vanguard.entity.CombinedSalesSummary;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface TotalSalesMapper {
  TotalSalesDto mapToTotalSalesDto(CombinedSalesSummary combinedSalesSummary);
}
