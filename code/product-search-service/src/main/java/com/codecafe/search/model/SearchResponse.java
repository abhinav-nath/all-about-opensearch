package com.codecafe.search.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.List;

@Getter
@With
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponse {

    private long totalResults;
    private List<Product> products;

}