package com.codecafe.search.helper;

import java.util.List;

import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.query_dsl.MatchQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.codecafe.search.config.OpenSearchConfiguration;
import com.codecafe.search.model.FacetData;

@Component
@RequiredArgsConstructor
public class SearchRequestBuilder {

  private final FacetsBuilder facetsBuilder;
  private final OpenSearchConfiguration openSearchConfiguration;

  public SearchRequest buildTextSearchRequest(String searchText, List<FacetData> facets, int page, int size) {

    Query byName = MatchQuery.of(m -> m
      .field("name")
      .query(FieldValue.of(searchText))
    )._toQuery();

    return new SearchRequest.Builder()
      .query(q -> q.bool(b -> b.should(byName)))
      .build();
  }

}
