package com.codecafe.search.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductDocument {

  private String code;
  private String name;
  private String description;
  private String brand;
  private String color;
  private String department;

  private double length;

  @JsonProperty("length_inches")
  private double lengthInInches;

  @JsonProperty("length_centimetres")
  private double lengthInCentimetres;

  private double width;

  @JsonProperty("width_inches")
  private double widthInInches;

  @JsonProperty("width_centimetres")
  private double widthInCentimetres;

  private double height;

  @JsonProperty("height_inches")
  private double heightInInches;

  @JsonProperty("height_centimetres")
  private double heightInCentimetres;

  private double weight;

  @JsonProperty("weight_pounds")
  private double weightInPounds;

  @JsonProperty("weight_kilograms")
  private double weightInKilograms;

  private BigDecimal price;
  private Boolean inStock;
  private Long createdAt;
  private Long modifiedAt;

}