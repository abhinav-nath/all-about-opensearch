package com.codecafe.search.helper;

import java.util.List;
import java.util.Map;

import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.query_dsl.MatchQuery;
import org.opensearch.client.opensearch._types.query_dsl.MultiMatchQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch._types.query_dsl.TextQueryType;
import org.opensearch.client.opensearch._types.query_dsl.WildcardQuery;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.codecafe.search.config.OpenSearchConfiguration;
import com.codecafe.search.model.FacetData;

@Component
@RequiredArgsConstructor
public class SearchRequestBuilder {

  private final FacetsBuilder facetsBuilder;
  private final OpenSearchConfiguration openSearchConfiguration;

  @Value("#{${app.search.query-boost-fields}}")
  private Map<String, Float> queryBoostFields;

  public SearchRequest buildSuggestionsRequest(String query) {
    Query multiMatch = MultiMatchQuery.of(m -> m.fields(List.of("name", "name._2gram", "name._3gram"))
                                                .query(query)
                                                .type(TextQueryType.BoolPrefix))
                                      ._toQuery();

    return new SearchRequest.Builder().query(multiMatch).build();
  }

  public SearchRequest buildTextSearchRequest(String searchText, List<FacetData> facets, int page, int size) {

    Query matchCode = MatchQuery.of(m -> m.field("code")
                                          .query(FieldValue.of(searchText))
                                          .boost(queryBoostFields.get("codeBoost"))
    )._toQuery();

    Query matchName = MatchQuery.of(m -> m.field("name")
                                          .query(FieldValue.of(searchText))
                                          .boost(queryBoostFields.get("nameBoost"))
    )._toQuery();

    Query matchNameWildcard = WildcardQuery.of(m -> m.field("name")
                                                     .boost(queryBoostFields.get("wildcardBoost"))
                                                     .value("*" + searchText + "*")
    )._toQuery();

    return new SearchRequest.Builder().query(q -> q.bool(b -> b.should(matchCode)
                                                               .should(matchName)
                                                               .should(matchNameWildcard)))
                                      .build();
  }

}
