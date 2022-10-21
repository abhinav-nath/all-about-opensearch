package com.codecafe.search.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProductHit {

  private String code;
  private String name;
  private String description;
  private String brand;
  private String color;
  private String department;
  private Long createdAt;
  private Long modifiedAt;

}