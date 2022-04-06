package com.codecafe.search.config;

import com.codecafe.search.document.ProductDocument;
import com.codecafe.search.service.OpenSearchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.action.index.IndexRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.opensearch.common.xcontent.XContentType.JSON;

@Slf4j
@Component
public class TestDataConfig {

  @Value("classpath:${app.search.ingestion.test-data-file:products.json}")
  private Resource testDataResource;

  private final OpenSearchService openSearchService;
  private final ObjectMapper objectMapper;

  @Autowired
  public TestDataConfig(OpenSearchService openSearchService, ObjectMapper objectMapper) {
    this.openSearchService = openSearchService;
    this.objectMapper = objectMapper;
  }

  @PostConstruct
  public void init() {
    try {
      openSearchService.deleteIndicesIfAlreadyPresent();

      ProductDocument[] productDocuments = objectMapper.readValue(testDataResource.getInputStream(), ProductDocument[].class);

      List<IndexRequest> indexRequests = new ArrayList<>();
      for (ProductDocument productDocument : productDocuments) {
        IndexRequest indexRequest = prepareIndexDocumentRequest(productDocument);
        indexRequests.add(indexRequest);
      }

      openSearchService.createIndices();
      openSearchService.bulkDocWrite(indexRequests);
      log.info("Successfully ingested test data");
    } catch (Exception ex) {
      log.error("Failed to create test data. Reason : {}", ex.getMessage());
    }
  }

  private IndexRequest prepareIndexDocumentRequest(ProductDocument productDocument) throws IOException {
    final IndexRequest indexRequest = new IndexRequest();
    indexRequest.id(String.valueOf(productDocument.getCode()));

    productDocument.setCreatedAt(Instant.now().toEpochMilli());
    productDocument.setModifiedAt(Instant.now().toEpochMilli());
    String productDocumentStr = objectMapper.writeValueAsString(productDocument);

    indexRequest.source(productDocumentStr, JSON);
    return indexRequest;
  }

}