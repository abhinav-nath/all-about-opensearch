## Product Search using OpenSearch Java high-level REST client and Search Templates

This small project demonstrates following functionalities and features:

1. Text search
2. OpenSearch query using [Search Template](./src/main/resources/mustache-template/text-search-template.mustache)
2. Facets
   1. Dynamic multi-select facets
   2. Search Filters
3. Pagination
4. Synonyms
5. Stemmers
6. Stop words
7. Did you mean
8. Test data ingestion

```shell
docker-compose up -d
```

Above command starts the following containers on your system:

1. OpenSearch
2. Postgres DB

## Postman collection

Postman collection JSON is located [here](./src/main/resources/product_catalog/postman_collection.json "Postman Collection").

Import it in Postman to see all the available endpoints.

## Test data setup

[products.json](./src/main/resources/products.json "Products") contain all the products which are indexed into OpenSearch.
