package com.codecafe.search.config;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import lombok.Getter;
import lombok.Setter;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.codecafe.search.repository")
@ComponentScan(basePackages = {"com.codecafe.search"})
public class OpenSearchConfig {

  @Value("${app.search.index-name}")
  private String indexName;

  private final OpenSearchProperties openSearchProperties;

  @Autowired
  public OpenSearchConfig(OpenSearchProperties openSearchProperties) {
    this.openSearchProperties = openSearchProperties;
  }

  @Bean
  public RestHighLevelClient restHighLevelClient() {
    final ClientConfiguration clientConfiguration = ClientConfiguration.builder()
      .connectedTo(openSearchProperties.getHostPort())
      .usingSsl(createSSLContext(), (hostname, sslSession) -> true)
      .withBasicAuth(openSearchProperties.getUsername(), openSearchProperties.getPassword())
      .build();

    return RestClients.create(clientConfiguration).rest();
  }

  @Bean
  public ElasticsearchRestTemplate elasticsearchTemplate() {
    return new ElasticsearchRestTemplate(restHighLevelClient());
  }

  @Bean
  public String indexName() {
    return indexName;
  }

  private SSLContext createSSLContext() {
    try {
      SSLContext sslContext = SSLContext.getInstance("TLS");
      sslContext.init(null, trustAllCerts, null);
      return sslContext;
    } catch (Exception e) {
      System.out.println(e.getCause());
    }
    return null;
  }

  private static final TrustManager[] trustAllCerts = new TrustManager[]{
    new X509TrustManager() {
      public java.security.cert.X509Certificate[] getAcceptedIssuers() {
        return null;
      }

      public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
      }

      public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
      }
    }
  };

  @Configuration
  @EnableConfigurationProperties
  @ConfigurationProperties(prefix = "app.search.os")
  @Getter
  @Setter
  public static class OpenSearchProperties {
    private String hostPort;
    private String username;
    private String password;
  }

}
