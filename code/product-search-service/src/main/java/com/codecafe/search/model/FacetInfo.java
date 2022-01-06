package com.codecafe.search.model;

import lombok.*;

@Getter
@With
@NoArgsConstructor
@AllArgsConstructor
public class FacetInfo {

    private String displayName;
    private String fieldName;

}