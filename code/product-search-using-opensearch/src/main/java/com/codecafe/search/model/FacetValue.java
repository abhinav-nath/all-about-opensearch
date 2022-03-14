package com.codecafe.search.model;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacetValue {

    private String name;
    private long count;

}