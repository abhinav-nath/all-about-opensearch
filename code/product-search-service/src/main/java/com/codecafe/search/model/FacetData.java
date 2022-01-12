package com.codecafe.search.model;

import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacetData {

    private String code;
    private List<String> values;

}