package com.codecafe.search.config;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestClientBuilder;
import org.opensearch.client.RestHighLevelClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import static org.apache.http.conn.ssl.NoopHostnameVerifier.INSTANCE;

@Slf4j
@Getter
@Setter
@Configuration
@RequiredArgsConstructor
public class OpenSearchConfiguration {

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

  private final OpenSearchProperties openSearchProperties;

  @Bean
  public RestHighLevelClient restHighLevelClient() {
    final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

    credentialsProvider.setCredentials(AuthScope.ANY,
      new UsernamePasswordCredentials(openSearchProperties.getUsername(), openSearchProperties.getPassword()));

    RestClientBuilder builder = RestClient.builder(new HttpHost(openSearchProperties.getHost(), openSearchProperties.getPort(), "https"))
                                          .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
                                            .setDefaultCredentialsProvider(credentialsProvider)
                                            .setSSLHostnameVerifier(INSTANCE)
                                            .setSSLContext(createSSLContext()));
    return new RestHighLevelClient(builder);
  }

  private SSLContext createSSLContext() {
    try {
      SSLContext sslContext = SSLContext.getInstance("TLS");
      sslContext.init(null, TRUST_ALL_CERTS, null);
      return sslContext;
    } catch (KeyManagementException | NoSuchAlgorithmException generalSecurityException) {
      log.error("failed to create ssl context", generalSecurityException);
    }
    return null;
  }

  @Getter
  @Setter
  @Configuration
  @ConfigurationProperties(prefix = "app.opensearch")
  public static class OpenSearchProperties {
    private String username;
    private String password;
    private String host;
    private int port;
    private String indexName;
    private String indexSource;
  }

}