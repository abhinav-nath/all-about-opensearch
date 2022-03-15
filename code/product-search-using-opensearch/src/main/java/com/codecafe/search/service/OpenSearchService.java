package com.codecafe.search.service;

import com.codecafe.search.config.OpenSearchConfig;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.OpenSearchException;
import org.opensearch.action.admin.indices.delete.DeleteIndexRequest;
import org.opensearch.action.bulk.BulkRequest;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.client.indices.CreateIndexRequest;
import org.opensearch.client.indices.CreateIndexResponse;
import org.opensearch.client.indices.GetIndexRequest;
import org.opensearch.indices.IndexCreationException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static java.nio.charset.Charset.defaultCharset;
import static org.opensearch.client.RequestOptions.DEFAULT;
import static org.opensearch.common.xcontent.XContentType.JSON;
import static org.springframework.util.StreamUtils.copyToString;

@Slf4j
@Service
public class OpenSearchService {

    private final RestHighLevelClient restHighLevelClient;
    private final OpenSearchConfig.OpenSearchProperties openSearchProperties;
    private final String indexName;

    public OpenSearchService(RestHighLevelClient restHighLevelClient, OpenSearchConfig.OpenSearchProperties openSearchProperties) {
        this.restHighLevelClient = restHighLevelClient;
        this.openSearchProperties = openSearchProperties;
        this.indexName = openSearchProperties.getIndexName();
    }

    public void bulkDocWrite(List<IndexRequest> indexRequests) {
        BulkRequest bulkRequest = new BulkRequest(indexName);
        try {
            indexRequests.forEach(bulkRequest::add);
            restHighLevelClient.bulk(bulkRequest, DEFAULT);
            /* Use for debugging
            if (bulkResponse != null) {
                String failureMessage = bulkResponse.buildFailureMessage();
                if (StringUtils.isNotEmpty(failureMessage)) {
                    log.error("Bulk Request failed with error : {}", failureMessage);
                }
            }*/
        } catch (Exception ex) {
            throw new OpenSearchException("Failed to write bulk request for index {}", ex, indexName);
        }
    }

    public void deleteIndexIfExists() {
        try {
            if (isIndexAlreadyPresent()) {
                deleteIndex();
            }
        } catch (IOException ioException) {
            throw new OpenSearchException("Failed to delete index `{}`", ioException, indexName);
        }
    }

    public void deleteIndex() throws IOException {
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(indexName);
        restHighLevelClient.indices().delete(deleteIndexRequest, DEFAULT);
    }

    public void createIndex() {
        try {
            CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
            String source = readFileFromClasspath(openSearchProperties.getSourcePath());
            createIndexRequest.source(source, JSON);
            CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(createIndexRequest, DEFAULT);
            if (!createIndexResponse.isAcknowledged()) {
                throw new IndexCreationException(indexName, new RuntimeException("Create index response is not acknowledged"));
            }
        } catch (IOException ioException) {
            throw new IndexCreationException(indexName, ioException);
        }
    }

    private boolean isIndexAlreadyPresent() throws IOException {
        GetIndexRequest getIndexRequest = new GetIndexRequest(indexName);
        return restHighLevelClient.indices().exists(getIndexRequest, DEFAULT);
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

}