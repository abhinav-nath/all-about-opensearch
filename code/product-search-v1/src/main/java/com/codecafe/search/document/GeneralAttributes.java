package com.codecafe.search.document;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static org.springframework.data.elasticsearch.annotations.FieldType.Keyword;
import static org.springframework.data.elasticsearch.annotations.FieldType.Text;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneralAttributes {

  private List<String> color;

  @MultiField(mainField = @Field(type = Text, fielddata = true), otherFields = {@InnerField(suffix = "raw", type = Keyword)})
  private List<String> colorFamily;

}
