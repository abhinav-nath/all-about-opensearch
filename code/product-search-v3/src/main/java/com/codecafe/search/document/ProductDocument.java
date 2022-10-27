package com.codecafe.search.document;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDocument {

  private String code;
  private String name;
  private String description;
  private String fullText;
  private String fullTextBoosted;
  private String brand;
  private String color;
  private String department;
  private BigDecimal price;
  private Boolean inStock;
  private Long createdAt;
  private Long modifiedAt;

  public void setFullTexts() {
    List<String> fullTextList = new ArrayList<>();
    fullTextList.add(this.description);
    fullTextList.add(this.department);
    this.fullText = fullTextList.stream().collect(Collectors.joining(" "));

    List<String> fullTextBoostedList = new ArrayList<>();

    fullTextBoostedList.add(this.name);
    fullTextBoostedList.add(this.brand);
    fullTextBoostedList.add(this.color);

    this.fullTextBoosted = fullTextBoostedList.stream().collect(Collectors.joining(" "));
  }

}