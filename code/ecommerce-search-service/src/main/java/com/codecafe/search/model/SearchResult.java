package com.codecafe.search.model;

import com.codecafe.search.document.ProductDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResult {

    private long totalResults;
    private List<ProductDocument> productDocuments;

    public SearchResponse toDto() {
        SearchResponse searchResponse = new SearchResponse();
        List<Product> products = new ArrayList<>(1);

        if (!CollectionUtils.isEmpty(productDocuments)) {
            productDocuments.forEach(p -> products.add(p.toDto()));
        }

        return searchResponse.withProducts(products).withTotalResults(totalResults);
    }

}