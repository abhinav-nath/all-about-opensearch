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
public class FacetData {

    private String code;
    private List<String> values;

}