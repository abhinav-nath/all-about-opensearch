package com.codecafe.search.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.codecafe.search.document.ProductDocument;

@Repository
public interface ProductRepository extends ElasticsearchRepository<ProductDocument, String> {

}
