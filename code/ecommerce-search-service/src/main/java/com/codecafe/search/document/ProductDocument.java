package com.codecafe.search.document;

import com.codecafe.search.model.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import static org.springframework.data.elasticsearch.annotations.FieldType.Boolean;
import static org.springframework.data.elasticsearch.annotations.FieldType.Double;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "#{@indexName}")
public class ProductDocument {

    @Id
    private String id;

    private String name;
    private String description;
    private List<String> categories;

    @Field(type = Double)
    private BigDecimal price;

    private String brand;
    private String color;

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
                .color(color)
                .dateAdded(dateAdded)
                .dateModified(dateModified)
                .isInStock(isInStock)
                .build();
    }

}