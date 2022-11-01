package com.codecafe.search.config;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

import com.codecafe.search.entity.FacetsConfig;
import com.codecafe.search.repository.FacetsConfigRepository;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import static org.springframework.util.CollectionUtils.isEmpty;

@Configuration
@RequiredArgsConstructor
@EnableJpaRepositories("com.codecafe.search.repository")
public class FacetsConfiguration {

  private final Map<String, FacetsConfig> facets;
  private final FacetsConfigRepository facetsConfigRepository;

  @PostConstruct
  private void init() {
    List<FacetsConfig> facetsConfigRecords = facetsConfigRepository.findAll();

    if (!isEmpty(facetsConfigRecords))
      facetsConfigRecords.forEach(facetsConfigRecord -> facets.put(facetsConfigRecord.getCode(), facetsConfigRecord));
  }

  public Map<String, FacetsConfig> getFacets() {
    return facets;
  }

}