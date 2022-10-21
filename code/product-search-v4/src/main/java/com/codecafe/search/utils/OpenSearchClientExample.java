package com.codecafe.search.utils;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.opensearch.client.RestClient;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.aggregations.Aggregate;
import org.opensearch.client.opensearch._types.aggregations.Aggregation;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.indices.CreateIndexRequest;
import org.opensearch.client.opensearch.indices.DeleteIndexRequest;
import org.opensearch.client.opensearch.indices.DeleteIndexResponse;
import org.opensearch.client.transport.OpenSearchTransport;
import org.opensearch.client.transport.rest_client.RestClientTransport;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import static org.apache.http.conn.ssl.NoopHostnameVerifier.INSTANCE;

public class OpenSearchClientExample {

  private static final TrustManager[] TRUST_ALL_CERTS = new TrustManager[]{
    new X509TrustManager() {
      public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[]{};
      }

      public void checkClientTrusted(X509Certificate[] certs, String authType) {
        /* trust all */
      }

      public void checkServerTrusted(X509Certificate[] certs, String authType) {
        /* trust all */
      }
    }
  };

  public static void main(String[] args) throws InterruptedException {
    RestClient restClient = null;
    try {
      //Only for demo purposes. Don't specify your credentials in code.
      final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
      credentialsProvider.setCredentials(AuthScope.ANY,
        new UsernamePasswordCredentials("admin", "admin"));

      //Initialize the client with SSL and TLS enabled
      restClient = RestClient.builder(new HttpHost("localhost", 9200, "https"))
                             .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
                                                                                                .setSSLHostnameVerifier(INSTANCE)
                                                                                                .setSSLContext(createSSLContext()))
                             .build();
      OpenSearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
      OpenSearchClient client = new OpenSearchClient(transport);

      //Create the index
      String index = "sample-index";
      CreateIndexRequest createIndexRequest = new CreateIndexRequest.Builder().index(index).build();
      client.indices().create(createIndexRequest);

      //Index some data
      IndexData indexData = new IndexData("John", "Doe");
      IndexRequest<IndexData> indexRequest = new IndexRequest.Builder<IndexData>().index(index).id("1").document(indexData).build();
      client.index(indexRequest);

      indexData = new IndexData("John", "Joe");
      indexRequest = new IndexRequest.Builder<IndexData>().index(index).id("2").document(indexData).build();
      client.index(indexRequest);

      Thread.sleep(1000);

      //Search for the document
      SearchRequest searchRequest = new SearchRequest.Builder().query(q -> q.match(m -> m.field("firstName")
                                                                                         .query(FieldValue.of("John"))))
                                                               .build();

      SearchResponse<IndexData> searchResponse = client.search(searchRequest, IndexData.class);
      System.out.println("Search Response:");
      for (int i = 0; i < searchResponse.hits().hits().size(); i++) {
        System.out.println("Search Hit #" + (i + 1) + " " + searchResponse.hits().hits().get(i).source());
      }

      //Aggregation
      searchRequest = new SearchRequest.Builder().query(q -> q.match(m -> m.field("firstName")
                                                                           .query(FieldValue.of("John"))))
                                                 .aggregations("firstNames", new Aggregation.Builder().terms(t -> t.field("firstName.keyword"))
                                                                                                      .build())
                                                 .build();

      searchResponse = client.search(searchRequest, IndexData.class);
      System.out.println("Search Response:");
      for (Map.Entry<String, Aggregate> entry : searchResponse.aggregations().entrySet()) {
        System.out.println("Agg - " + entry.getKey());
        entry.getValue().sterms().buckets().array().forEach(b -> System.out.printf("%s : %d%n", b.key(), b.docCount()));
      }

      //Delete the document
      client.delete(b -> b.index(index).id("1"));

      // Delete the index
      DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest.Builder().index(index).build();
      DeleteIndexResponse deleteIndexResponse = client.indices().delete(deleteIndexRequest);

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (restClient != null) {
          restClient.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private static SSLContext createSSLContext() {
    try {
      SSLContext sslContext = SSLContext.getInstance("TLS");
      sslContext.init(null, TRUST_ALL_CERTS, null);
      return sslContext;
    } catch (KeyManagementException | NoSuchAlgorithmException generalSecurityException) {
      generalSecurityException.printStackTrace();
    }
    return null;
  }

  @Getter
  @Setter
  @ToString
  @NoArgsConstructor
  @AllArgsConstructor
  static class IndexData {

    private String firstName;
    private String lastName;

  }

}
