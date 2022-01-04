package com.codecafe.search.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Facet {

    private String name;
    private List<FacetValue> facetValues;

}