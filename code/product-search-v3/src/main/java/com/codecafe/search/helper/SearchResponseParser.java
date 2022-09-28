package com.codecafe.search.helper;

import java.util.ArrayList;
import java.util.List;

import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.Hit;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.codecafe.search.config.FacetsConfiguration;
import com.codecafe.search.document.ProductDocument;
import com.codecafe.search.model.ProductData;
import com.codecafe.search.model.TextSearchResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class SearchResponseParser {

  private final ObjectMapper objectMapper;
  private final FacetsConfiguration facetsConfiguration;

  public TextSearchResponse toTextSearchResponse(SearchResponse<ProductDocument> searchResponse) {

    List<ProductData> productDataList = new ArrayList<>();
    List<Hit<ProductDocument>> hits = searchResponse.hits().hits();
    for (Hit<ProductDocument> hit : hits) {
      ProductDocument product = hit.source();
      log.info("Found product " + product.getName() + ", score " + hit.score());

      ProductData productData = ProductData.builder()
                                           .code(product.getCode())
                                           .name(product.getName())
                                           .description(product.getDescription())
                                           .brand(product.getBrand())
                                           .color(product.getColor())
                                           .department(product.getDepartment())
                                           .build();
      productDataList.add(productData);
    }

    return TextSearchResponse.builder()
                             .totalResults(searchResponse.hits().total().value())
                             .products(productDataList)
                             .build();
  }

}
