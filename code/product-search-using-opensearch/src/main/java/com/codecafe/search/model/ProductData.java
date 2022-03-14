package com.codecafe.search.model;

import lombok.*;

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