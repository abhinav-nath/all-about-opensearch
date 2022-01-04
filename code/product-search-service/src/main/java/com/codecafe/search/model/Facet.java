package com.codecafe.search.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;

@Getter
@With
@NoArgsConstructor
@AllArgsConstructor
public class Facet {

    private String name;
    private long count;

}