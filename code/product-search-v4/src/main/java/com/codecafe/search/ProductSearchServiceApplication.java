package com.codecafe.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class ProductSearchServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(ProductSearchServiceApplication.class, args);
  }

}
