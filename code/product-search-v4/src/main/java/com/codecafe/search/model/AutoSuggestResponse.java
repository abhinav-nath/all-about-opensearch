package com.codecafe.search.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static java.util.Collections.emptyList;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutoSuggestResponse {

  @Builder.Default
  private List<String> suggestions = emptyList();

}
