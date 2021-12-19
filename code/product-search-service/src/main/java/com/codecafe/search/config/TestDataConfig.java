package com.codecafe.search.config;

import com.codecafe.search.document.ProductDocument;
import com.codecafe.search.repository.ProductRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.elasticsearch.core.completion.Completion;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@ConditionalOnProperty(prefix = "app.search", name = "create-test-data", matchIfMissing = true)
public class TestDataConfig {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TestDataConfig.class);

    private final ProductRepository productRepository;

    @Autowired
    public TestDataConfig(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @PostConstruct
    public void init() {
        try {
            productRepository.deleteAll();
            List<ProductDocument> productDocuments = buildProductDocuments();
            productRepository.saveAll(productDocuments);
        } catch (Exception ex) {
            logger.error("Failed to create test data. Reason : {}", ex.getMessage());
        }
    }

    private List<ProductDocument> buildProductDocuments() {
        List<ProductDocument> productDocuments = new ArrayList<>();

        productDocuments.add(ProductDocument.builder()
                .id("1")
                .name("Macbook Pro 2019")
                .brand("Apple")
                .categories(List.of("Laptops", "Productivity Laptops"))
                .description("Apple Macbook Pro 2019 model")
                .suggest(new Completion(List.of("macbook", "pro", "productivity", "laptops", "laptop")))
                .color("Silver")
                .price(BigDecimal.valueOf(2200))
                .dateAdded(ZonedDateTime.now())
                .isInStock(true)
                .build());

        productDocuments.add(ProductDocument.builder()
                .id("2")
                .name("ROG Zephyrus")
                .brand("Asus")
                .categories(List.of("Laptops", "Gaming Laptops"))
                .description("Asus ROG Zephyrus Ryzen 9")
                .suggest(new Completion(List.of("asus", "rog", "zephyrus", "ryzen", "gaming")))
                .color("Black")
                .price(BigDecimal.valueOf(1550))
                .dateAdded(ZonedDateTime.now())
                .isInStock(true)
                .build());

        return productDocuments;
    }

}