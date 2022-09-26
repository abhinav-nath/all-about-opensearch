package com.codecafe.search.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.elasticsearch.core.completion.Completion;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import com.codecafe.search.document.ProductDocument;
import com.codecafe.search.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.SPACE;

@Component
@ConditionalOnProperty(prefix = "app.search", name = "create-test-data", matchIfMissing = true)
public class TestDataConfig {

  private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TestDataConfig.class);

  private final ProductRepository productRepository;
  private final ObjectMapper objectMapper;

  @Autowired
  public TestDataConfig(ProductRepository productRepository, ObjectMapper objectMapper) {
    this.productRepository = productRepository;
    this.objectMapper = objectMapper;
  }

  @PostConstruct
  public void init() {
    try {
      productRepository.deleteAll();
      List<ProductDocument> productDocuments = Arrays.asList(objectMapper.readValue(ResourceUtils.getFile("classpath:" + "product_catalog/products.json"), ProductDocument[].class));
      productDocuments.forEach(this::addSuggestions);
      productRepository.saveAll(productDocuments);
    } catch (Exception ex) {
      logger.error("Failed to create test data. Reason : {}", ex.getMessage());
    }
  }

  private void addSuggestions(ProductDocument productDocument) {
    List<String> suggestions = new ArrayList<>(asList(productDocument.getName().toLowerCase().split(SPACE)));

    for (String category : productDocument.getCategories()) {
      suggestions.addAll(asList(category.toLowerCase().split(SPACE)));
    }

    Set<String> distinctKeywords = new HashSet<>(suggestions);
    productDocument.setSuggest(new Completion(new ArrayList<>(distinctKeywords)));
  }

}
