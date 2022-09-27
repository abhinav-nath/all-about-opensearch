package com.codecafe.search.service;

import jakarta.json.stream.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import org.opensearch.client.json.JsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.mapping.TypeMapping;
import org.opensearch.client.opensearch.core.BulkRequest;
import org.opensearch.client.opensearch.core.BulkResponse;
import org.opensearch.client.opensearch.indices.CreateIndexRequest;
import org.opensearch.client.opensearch.indices.CreateIndexResponse;
import org.opensearch.client.opensearch.indices.DeleteIndexRequest;
import org.opensearch.client.opensearch.indices.ExistsRequest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import com.codecafe.search.config.OpenSearchConfiguration;

import static java.lang.Boolean.FALSE;
import static java.nio.charset.Charset.defaultCharset;
import static org.springframework.util.StreamUtils.copyToString;

@Slf4j
@Service
public class OpenSearchService {

  private final OpenSearchClient openSearchClient;
  private final OpenSearchConfiguration openSearchConfiguration;
  private final OpenSearchConfiguration.OpenSearchProperties openSearchProperties;

  public OpenSearchService(OpenSearchClient openSearchClient, OpenSearchConfiguration openSearchConfiguration) {
    this.openSearchClient = openSearchClient;
    this.openSearchConfiguration = openSearchConfiguration;
    this.openSearchProperties = openSearchConfiguration.getOpenSearchProperties();
  }

  @Nullable
  private static String readFileFromClasspath(String url) {
    ClassPathResource classPathResource = new ClassPathResource(url);
    try (InputStream is = classPathResource.getInputStream()) {
      return copyToString(is, defaultCharset());
    } catch (Exception e) {
      log.error(String.format("Failed to load file from url: %s: %s", url, e.getMessage()));
      return null;
    }
  }

  public void bulkDocWrite(BulkRequest bulkRequest) {
    try {
      BulkResponse bulkResponse = openSearchClient.bulk(bulkRequest);
      if (bulkResponse != null && bulkResponse.errors()) {
        log.error("Bulk Request failed");
      }
    } catch (IOException e) {
      log.error("Exception in Bulk operation");
      throw new RuntimeException(e);
    }
  }

  public void deleteIndexIfAlreadyPresent() {
    try {
      if (isIndexAlreadyPresent(openSearchProperties.getIndexName())) {
        deleteIndex(openSearchProperties.getIndexName());
      }
    } catch (IOException ioException) {
      log.error("Failed to delete index {}", openSearchProperties.getIndexName());
    }
  }

  public void deleteIndex(String indexName) throws IOException {
    DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest.Builder().index(indexName).build();
    openSearchClient.indices().delete(deleteIndexRequest);
  }

  public void createIndex() {
    try {
      String source = readFileFromClasspath(openSearchProperties.getIndexSource());
      ClassPathResource classPathResource = new ClassPathResource(openSearchProperties.getIndexSource());

      JsonpMapper mapper = openSearchClient._transport().jsonpMapper();
      JsonParser parser = mapper.jsonProvider()
                                .createParser(new StringReader(Files.readString(Path.of(Objects.requireNonNull(getClass()
                                                                                                 .getClassLoader()
                                                                                                 .getResource(openSearchProperties.getIndexSource()))
                                                                                               .getPath()))));

      CreateIndexRequest createIndexRequest = new CreateIndexRequest.Builder().index(openSearchProperties.getIndexName())
                                                                              .mappings(TypeMapping._DESERIALIZER.deserialize(parser, mapper))
                                                                              .build();
      CreateIndexResponse createIndexResponse = openSearchClient.indices().create(createIndexRequest);
      if (FALSE.equals(createIndexResponse.acknowledged())) {
        throw new RuntimeException("Create index response is not acknowledged");
      }
    } catch (Exception ex) {
      throw new RuntimeException("Failed to create index");
    }
  }

  private boolean isIndexAlreadyPresent(String indexName) throws IOException {
    ExistsRequest existsRequest = new ExistsRequest.Builder().index(indexName).build();
    return openSearchClient.indices().exists(existsRequest).value();
  }

}
