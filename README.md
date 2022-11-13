# All about OpenSearch (or ElasticSearch)

- [Analysis](notes/analysis.md)
- [Inverted Indices](notes/inverted-indices.md)
- [Index Mappings](notes/mapping.md)
- [Inspecting the cluster](notes/inspecting-the-cluster.md)
- [`object` field type](notes/object-field-type.md)
- [Array of objects and `nested` field type](notes/array-of-objects.md)
- [Autocomplete functionality](notes/opensearch-autocomplete-functionality.md)

## APIs

- [Index APIs](notes/apis/index-apis.md)
- [Document APIs](notes/apis/document-apis.md)
- [Search](notes/apis/search.md)

## Code

I have written multiple implementations of a dummy e-commerce search service using Java and Spring Boot:

- [product-search-v1](code/product-search-v1) : using [Spring Data Elasticsearch](https://spring.io/projects/spring-data-elasticsearch)
- [product-search-v2](code/product-search-v2) : using [OpenSearch Java high-level REST client](https://opensearch.org/docs/latest/clients/java-rest-high-level/)
- [product-search-v3](code/product-search-v3) : using [OpenSearch Java high-level REST client](https://opensearch.org/docs/1.3/clients/java-rest-high-level/) and [Search Templates](https://opensearch.org/docs/1.3/opensearch/search-template/)
- [product-search-v4](code/product-search-v4) : using [OpenSearch Java client](https://opensearch.org/docs/latest/clients/java/)
