package com.codecafe.search.document;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDocument {

  private String code;
  private String name;
  private String description;
  private String brand;
  private String color;
  private String department;
  private BigDecimal price;
  private Boolean inStock;
  private Long createdAt;
  private Long modifiedAt;

}