package com.codecafe.search.entity;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.vladmihalcea.hibernate.type.json.JsonType;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "facets_config")
@TypeDef(name = "json", typeClass = JsonType.class)
public class FacetsConfig {

  @Id
  private String code;

  private String displayName;
  private int sequence;

  private boolean isMeasurement;

  @Type(type = "json")
  @Column(columnDefinition = "jsonb")
  private Map<String, String> measurementUnits;

}