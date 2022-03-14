package com.codecafe.search.model;

import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponse {

    private long totalResults;
    private List<Product> products;
    private List<Facet> facets;

}