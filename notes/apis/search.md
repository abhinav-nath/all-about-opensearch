# Search

OS Search [documentation](https://opensearch.org/docs/latest/opensearch/ux/ "OpenSearch Search Experience")

`from` and `size` are for [pagination](https://opensearch.org/docs/latest/opensearch/ux/#paginate-results "Paginate Results")

```json
GET product-catalog-index/_search
{
  "from": 0,
  "size": 10,
  "query": {
    "match": {
      "category": "Laptops"
    }
  }
}
```

Facets:

```json
GET product-catalog-index/_search
{
  "from": 0,
  "size": 10,
  "query": {
    "match": {
      "name": "Macbook"
    }
  },
  "aggs": {
    "facets": {
      "nested": {
        "path": "category"
      },
      "aggs": {
        "super_category": {
          "terms": {
            "field": "category.super_category"
          }
        },
        "sub_categories": {
          "terms": {
            "field": "category.sub_categories"
          }
        }
      }
    }
  }
}
```