package com.codecafe.search.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

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
    private String color;
    private ZonedDateTime dateAdded;
    private ZonedDateTime dateModified;
    private Boolean isInStock;

}