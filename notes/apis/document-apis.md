# Document APIs

## Create a single document

```json
PUT product-catalog-index/_doc/1
{
  "name": "Macbook Pro 2021",
  "category": {
    "super_category": "Electronics",
    "sub_categories": [
      "Laptops",
      "Productivity Laptops"
    ]
  },
  "brand": "Apple",
  "description": "Apple Macbook Pro 2021 model with M1 Chip",
  "color": "Grey",
  "price": 1300,
  "date_added": "2021-08-22",
  "in_stock": true
}
```

## Get document

Request:

```json
GET product-catalog-index/_doc/1
```

Response:

```json
{
  "_index": "product-catalog-index",
  "_type": "_doc",
  "_id": "1",
  "_version": 1,
  "_seq_no": 0,
  "_primary_term": 1,
  "found": true,
  "_source": {
    "name": "Macbook Pro 2019",
    "category": {
      "super_category": "Electronics",
      "sub_categories": [
        "Laptops",
        "Productivity Laptops"
      ]
    },
    "brand": "Apple",
    "description": "Apple Macbook Pro 2019 model",
    "color": "Silver",
    "price": 2200,
    "date_added": "2019-04-18",
    "in_stock": false
  }
}
```

## Update document

```json
POST product-catalog-index/_update/1
{
  "doc": {
    "name": "Macbook Pro 2019",
    "category": {
      "super_category": "Electronics",
      "sub_categories": [
        "Laptops",
        "Productivity Laptops"
      ]
    },
    "brand": "Apple",
    "description": "Apple Macbook Pro 2019 model",
    "color": "Silver",
    "price": 2200,
    "date_added": "2019-04-18",
    "in_stock": false
  }
}
```

## Delete document

```json
DELETE /product-catalog-index/_doc/1
```

## Bulk operation

> The bulk operation lets you add, update, or delete many documents in a single request.
> Compared to individual OpenSearch indexing requests, the bulk operation has significant performance benefits.
> Whenever practical, we recommend batching indexing operations into bulk requests.

Example:

```json
POST _bulk
{ "delete": { "_index": "movies", "_id": "tt2229499" } }
{ "index": { "_index": "movies", "_id": "tt1979320" } }
{ "title": "Rush", "year": 2013 }
{ "create": { "_index": "movies", "_id": "tt1392214" } }
{ "title": "Prisoners", "year": 2013 }
{ "update": { "_index": "movies", "_id": "tt0816711" } }
{ "doc" : { "title": "World War Z" } }
```

If you need to perform bulk operations on a the same index then it is better to send the index in the path:

```json
POST <index>/_bulk
```

Example:

```json
POST product-catalog-index/_bulk
{"delete":{"_id":"1"}}
{"create":{"_id":"1"}}
{"name":"Macbook Pro 2019","category":{"super_category":"Electronics","sub_categories":["Laptops","Productivity Laptops"]},"brand":"Apple","description":"Apple Macbook Pro 2019 model","color":"Silver","price":2200,"date_added":"2019-04-18","in_stock":false}
```