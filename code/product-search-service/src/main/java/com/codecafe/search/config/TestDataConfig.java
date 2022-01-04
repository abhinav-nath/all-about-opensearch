package com.codecafe.search.config;

import com.codecafe.search.document.ProductDocument;
import com.codecafe.search.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@Component
@ConditionalOnProperty(prefix = "app.search", name = "create-test-data", matchIfMissing = true)
public class TestDataConfig {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TestDataConfig.class);

    private final ProductRepository productRepository;
    private ObjectMapper objectMapper;

    @Autowired
    public TestDataConfig(ProductRepository productRepository, ObjectMapper objectMapper) {
        this.productRepository = productRepository;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        try {
            productRepository.deleteAll();
            List<ProductDocument> productDocuments = Arrays.asList(objectMapper.readValue(ResourceUtils.getFile("classpath:" + "product_catalog/products.json"), ProductDocument[].class));
            productRepository.saveAll(productDocuments);
        } catch (Exception ex) {
            logger.error("Failed to create test data. Reason : {}", ex.getMessage());
        }
    }

}