package com.codecafe.search.config;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.opensearch.client.opensearch.core.BulkRequest;
import org.opensearch.client.opensearch.core.bulk.BulkOperation;
import org.opensearch.client.opensearch.core.bulk.CreateOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.codecafe.search.document.ProductDocument;
import com.codecafe.search.service.OpenSearchService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class TestDataConfiguration {

  private final OpenSearchService openSearchService;
  private final ObjectMapper objectMapper;
  private final OpenSearchConfiguration openSearchConfiguration;
  private final OpenSearchConfiguration.OpenSearchProperties openSearchProperties;


  @Value("classpath:${app.search.ingestion.test-data-file:products.json}")
  private Resource testDataResource;

  @PostConstruct
  public void init() {
    try {
      openSearchService.deleteIndexIfAlreadyPresent();

      ProductDocument[] productDocuments = objectMapper.readValue(testDataResource.getInputStream(), ProductDocument[].class);

      List<BulkOperation> bulkOperations = new ArrayList<>();
      for (ProductDocument productDocument : productDocuments) {
        BulkOperation createOperation = prepareCreateOperation(productDocument)._toBulkOperation();
        bulkOperations.add(createOperation);
      }

      BulkRequest bulkRequest = new BulkRequest.Builder().operations(bulkOperations)
                                                         .index(openSearchProperties.getIndexName())
                                                         .build();

      openSearchService.createIndex();
      openSearchService.bulkDocWrite(bulkRequest);
      log.info("Successfully ingested test data");
    } catch (Exception ex) {
      log.error("Failed to create test data. Reason : {}", ex.getMessage());
    }
  }

  private CreateOperation<ProductDocument> prepareCreateOperation(ProductDocument productDocument) throws IOException {
    productDocument.setCreatedAt(Instant.now().toEpochMilli());
    productDocument.setModifiedAt(Instant.now().toEpochMilli());

    return new CreateOperation.Builder<ProductDocument>().id(String.valueOf(productDocument.getCode()))
                                                         .document(productDocument)
                                                         .build();
  }

}
