package com.codecafe.search.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.codecafe.search.document.GeneralAttributes;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

  private String id;
  private String name;
  private String description;
  private List<String> categories;
  private BigDecimal price;
  private String brand;
  private GeneralAttributes generalAttributes;
  private ZonedDateTime dateAdded;
  private ZonedDateTime dateModified;
  private Boolean isInStock;

}
