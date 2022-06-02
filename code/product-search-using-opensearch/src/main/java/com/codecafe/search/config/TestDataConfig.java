package com.codecafe.search.config;

import com.codecafe.search.document.ProductDocument;
import com.codecafe.search.service.OpenSearchService;
import com.codecafe.search.utils.UnitConverter;
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

  private final UnitConverter unitConverter;

  @Autowired
  public TestDataConfig(OpenSearchService openSearchService, ObjectMapper objectMapper, UnitConverter unitConverter) {
    this.openSearchService = openSearchService;
    this.objectMapper = objectMapper;
    this.unitConverter = unitConverter;
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

    productDocument.setLengthInInches(productDocument.getLength());
    productDocument.setLengthInCentimetres(unitConverter.toCentimetres(productDocument.getLength()));
    productDocument.setWidthInInches(productDocument.getWidth());
    productDocument.setWidthInCentimetres(unitConverter.toCentimetres(productDocument.getWidth()));
    productDocument.setHeightInInches(productDocument.getHeight());
    productDocument.setHeightInCentimetres(unitConverter.toCentimetres(productDocument.getHeight()));
    productDocument.setWeightInPounds(productDocument.getWeight());
    productDocument.setWeightInKilograms(unitConverter.toKilograms(productDocument.getWeight()));

    String productDocumentStr = objectMapper.writeValueAsString(productDocument);

    indexRequest.source(productDocumentStr, JSON);
    return indexRequest;
  }

}