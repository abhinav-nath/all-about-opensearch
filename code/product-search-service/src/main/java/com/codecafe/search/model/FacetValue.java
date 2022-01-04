package com.codecafe.search.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;

@Getter
@With
@NoArgsConstructor
@AllArgsConstructor
public class FacetValue {

    private String name;
    private long count;

}