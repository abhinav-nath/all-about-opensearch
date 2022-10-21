package com.codecafe.search.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductData {

  private String code;
  private String name;
  private String description;
  private String brand;
  private String color;
  private String department;

}