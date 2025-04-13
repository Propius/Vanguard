package com.example.vanguard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.example.vanguard"})
@EnableCaching
@EnableScheduling
@EnableAsync(proxyTargetClass = true)
@EnableSpringDataWebSupport(
    pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class VanguardApplication {

  public static void main(String[] args) {
    SpringApplication.run(VanguardApplication.class, args);
  }
}
