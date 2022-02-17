package com.codecafe.search.document;

import com.codecafe.search.model.Product;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.core.completion.Completion;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import static org.springframework.data.elasticsearch.annotations.FieldType.*;

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