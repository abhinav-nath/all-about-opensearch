package com.codecafe.search.document;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.CompletionField;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;
import org.springframework.data.elasticsearch.core.completion.Completion;

import lombok.Getter;
import lombok.Setter;

import com.codecafe.search.model.Product;

import static org.springframework.data.elasticsearch.annotations.FieldType.Boolean;
import static org.springframework.data.elasticsearch.annotations.FieldType.Double;
import static org.springframework.data.elasticsearch.annotations.FieldType.Keyword;
import static org.springframework.data.elasticsearch.annotations.FieldType.Object;
import static org.springframework.data.elasticsearch.annotations.FieldType.Text;

@Getter
@Setter
@Document(indexName = "#{@indexName}")
public class ProductDocument {

  @Id
  private String id;

  private String name;
  private String description;

  @MultiField(mainField = @Field(type = Text, fielddata = true), otherFields = {@InnerField(suffix = "raw", type = Keyword)})
  private List<String> categories;

  @CompletionField
  private Completion suggest;

  @Field(type = Double)
  private BigDecimal price;

  @MultiField(mainField = @Field(type = Text, fielddata = true), otherFields = {@InnerField(suffix = "raw", type = Keyword)})
  private String brand;

  @Field(type = Object)
  private GeneralAttributes generalAttributes;

  @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSZZ")
  private ZonedDateTime dateAdded;

  @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSZZ")
  private ZonedDateTime dateModified;

  @Field(type = Boolean)
  private Boolean isInStock;

  public Product toDto() {
    return Product.builder()
      .id(id)
      .name(name)
      .categories(categories)
      .price(price)
      .brand(brand)
      .generalAttributes(generalAttributes)
      .dateAdded(dateAdded)
      .dateModified(dateModified)
      .isInStock(isInStock)
      .build();
  }

}
