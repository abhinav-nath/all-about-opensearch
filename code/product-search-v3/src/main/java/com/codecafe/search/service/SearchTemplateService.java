package com.codecafe.search.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.opensearch.OpenSearchException;
import org.opensearch.action.admin.cluster.storedscripts.PutStoredScriptRequest;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.action.support.master.AcknowledgedResponse;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.common.bytes.BytesReference;
import org.opensearch.common.xcontent.XContentBuilder;
import org.opensearch.common.xcontent.XContentFactory;
import org.opensearch.script.mustache.SearchTemplateRequest;
import org.opensearch.script.mustache.SearchTemplateResponse;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import com.codecafe.search.config.OpenSearchConfiguration.OpenSearchProperties;
import com.codecafe.search.helper.SearchTemplate;

import static java.nio.charset.Charset.defaultCharset;
import static org.opensearch.client.RequestOptions.DEFAULT;
import static org.opensearch.common.xcontent.XContentType.JSON;
import static org.opensearch.rest.RestStatus.OK;
import static org.opensearch.script.ScriptType.STORED;
import static org.springframework.util.StreamUtils.copyToString;

@Slf4j
@Service
public class SearchTemplateService {

  private static final String ERROR_EXECUTING_SEARCH_TEMPLATE_MSG = "Error executing search template";
  private static final String FAILED_TO_CREATE_SEARCH_TEMPLATE = "Failed to create {}";

  private final RestHighLevelClient restHighLevelClient;
  private final OpenSearchProperties openSearchProperties;

  public SearchTemplateService(RestHighLevelClient restHighLevelClient, OpenSearchProperties openSearchProperties) {
    this.restHighLevelClient = restHighLevelClient;
    this.openSearchProperties = openSearchProperties;
  }

  @PostConstruct
  public void createSearchTemplates() {
    Arrays.stream(SearchTemplate.values())
          .forEach(this::createSearchTemplateFor);
  }

  public SearchResponse executeTemplate(SearchTemplate searchTemplate, Map<String, Object> params) {
    Map<String, Object> templateParams = new HashMap<>(params);
    SearchTemplateRequest searchTemplateRequest = buildSearchTemplateRequest(searchTemplate, templateParams);
    SearchTemplateResponse searchTemplateResponse = null;

    try {
      searchTemplateResponse = restHighLevelClient.searchTemplate(searchTemplateRequest, DEFAULT);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    if (searchTemplateResponse.status() == OK) {
      return searchTemplateResponse.getResponse();
    }

    throw new OpenSearchException(ERROR_EXECUTING_SEARCH_TEMPLATE_MSG);
  }

  private SearchTemplateRequest buildSearchTemplateRequest(SearchTemplate searchTemplate, Map<String, Object> params) {
    SearchRequest searchRequest = new SearchRequest(openSearchProperties.getIndexName());
    SearchTemplateRequest searchTemplateRequest = new SearchTemplateRequest();
    searchTemplateRequest.setScriptType(STORED);
    searchTemplateRequest.setScript(searchTemplate.getId());
    searchTemplateRequest.setScriptParams(params);
    searchTemplateRequest.setRequest(searchRequest);
    return searchTemplateRequest;
  }

  private void createSearchTemplateFor(SearchTemplate searchTemplate) {
    try {
      PutStoredScriptRequest putStoredScriptRequest = buildStoredScriptRequestFor(searchTemplate);

      AcknowledgedResponse acknowledgedResponse = restHighLevelClient.putScript(putStoredScriptRequest, DEFAULT);

      if (!acknowledgedResponse.isAcknowledged()) {
        throw new OpenSearchException(FAILED_TO_CREATE_SEARCH_TEMPLATE, searchTemplate.getId());
      }
    } catch (IOException ioException) {
      throw new OpenSearchException(FAILED_TO_CREATE_SEARCH_TEMPLATE, ioException, searchTemplate.getId());
    }
  }

  private PutStoredScriptRequest buildStoredScriptRequestFor(SearchTemplate searchTemplate) throws IOException {
    PutStoredScriptRequest putStoredScriptRequest = new PutStoredScriptRequest();
    putStoredScriptRequest.id(searchTemplate.getId());

    XContentBuilder builder = createSearchTemplateScript(searchTemplate.getSourcePath());
    putStoredScriptRequest.content(BytesReference.bytes(builder), JSON);
    return putStoredScriptRequest;
  }

  private XContentBuilder createSearchTemplateScript(String templateSourceFilePath) throws IOException {
    XContentBuilder builder = XContentFactory.jsonBuilder();
    builder.startObject();
    builder.startObject("script");
    builder.field("lang", "mustache");
    builder.field("source", readFileFromClasspath(templateSourceFilePath));
    builder.endObject();
    builder.endObject();
    return builder;
  }

  private String readFileFromClasspath(String filePath) throws IOException {
    ClassPathResource classPathResource = new ClassPathResource(filePath);
    try (InputStream is = classPathResource.getInputStream()) {
      return copyToString(is, defaultCharset());
    }
  }

}