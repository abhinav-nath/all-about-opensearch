# Index APIs

## Create an Index with Mappings

```json
PUT /product-catalog-index
{
  "settings": {
    "index": {
      "number_of_shards": 2,
      "number_of_replicas": 1
    }
  },
  "mappings": {
    "properties": {
      "name": {
        "type": "text"
      },
      "category": {
        "type": "nested",
        "properties": {
          "super_category": {
            "type": "keyword"
          },
          "sub_categories": {
            "type": "keyword"
          }
        }
      },
      "brand": {
        "type": "keyword"
      },
      "description": {
        "type": "text"
      },
      "color": {
        "type": "text"
      },
      "price": {
        "type": "double"
      },
      "date_added": {
        "type": "date"
      },
      "date_modified": {
        "type": "date"
      },
      "in_stock": {
        "type": "boolean"
      }
    }
  },
  "aliases": {
    "product-catalog": {}
  }
}
```

## Get Index

```json
GET /product-catalog-index
```

## Check if Index exists

```json
HEAD /product-catalog-index
```

## Delete an Index

```json
DELETE /product-catalog-index
```

## Close index

The close index API operation closes an index.
Once an index is closed, you cannot add data to it or search for any data within the index.

```json
POST /product-catalog-index/_close
```