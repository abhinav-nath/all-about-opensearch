package com.codecafe.search.helper;

import org.opensearch.client.opensearch.indices.CreateIndexRequest;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.codecafe.search.config.OpenSearchConfiguration;

@Component
@RequiredArgsConstructor
public class IndexBuilder {

  private final OpenSearchConfiguration.OpenSearchProperties openSearchProperties;

  public CreateIndexRequest buildCreateIndexRequest() {
    return new CreateIndexRequest.Builder().index(openSearchProperties.getIndexName())
                                           .mappings(m -> m.properties("code", p -> p.text(t -> t))
                                                           .properties("name", p -> p.searchAsYouType(s -> s))
                                                           .properties("brand", p1 -> p1.text(t -> t.fields("raw", p2 -> p2.keyword(k -> k))))
                                                           .properties("description", p -> p.text(t -> t))
                                                           .properties("department", p1 -> p1.text(t -> t.fields("raw", p2 -> p2.keyword(k -> k))))
                                                           .properties("color", p1 -> p1.text(t -> t.fields("raw", p2 -> p2.keyword(k -> k))))
                                                           .properties("price", p -> p.double_(d -> d))
                                                           .properties("inStock", p -> p.boolean_(b -> b))
                                                           .properties("createdAt", p -> p.long_(l -> l))
                                                           .properties("modifiedAt", p -> p.long_(l -> l))
                                           )
                                           .build();
  }

}
