# Array of objects and `nested` field type

## How arrays of objects are flattened

Elasticsearch has no concept of inner objects.
Therefore, it flattens object hierarchies into a simple list of field names and values. For instance, consider the following document:

```json
PUT my-index-000001/_doc/1
{
  "group" : "fans",
  "user" : [
    {
      "first" : "John",
      "last" :  "Smith"
    },
    {
      "first" : "Alice",
      "last" :  "White"
    }
  ]
}
```

The `user` field is dynamically added as a field of type `object`.

This document would be transformed internally into a document that looks more like this:

```json
{
  "group" :        "fans",
  "user.first" : [ "alice", "john" ],
  "user.last" :  [ "smith", "white" ]
}
```

The `user.first` and `user.last` fields are flattened into multi-value fields, and the association between `alice` and `white` is lost.
This document would incorrectly match a query for `alice` AND `smith`:

```json
GET my-index-000001/_search
{
  "query": {
    "bool": {
      "must": [
        { "match": { "user.first": "Alice" }},
        { "match": { "user.last":  "Smith" }}
      ]
    }
  }
}
```

Result:

```json
{
  "took" : 7,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 1,
      "relation" : "eq"
    },
    "max_score" : 0.5753642,
    "hits" : [
      {
        "_index" : "my-index-000001",
        "_type" : "_doc",
        "_id" : "1",
        "_score" : 0.5753642,
        "_source" : {
          "group" : "fans",
          "user" : [
            {
              "first" : "John",
              "last" : "Smith"  
            },
            {
              "first" : "Alice",
              "last" : "White"
            }
          ]
        }
      }
    ]
  }
}
```

## Using `nested` field type for arrays of objects

If you need to index arrays of objects and to maintain the independence of each object in the array, use the `nested` data type instead of the `object` data type.

Internally, nested objects index each object in the array as a separate hidden document, meaning that each nested object can be queried independently of the others with the `nested query`:


The user field is mapped as type `nested` instead of type `object`.

```json
PUT my-index-000001
{
  "mappings": {
    "properties": {
      "user": {
        "type": "nested"
      }
    }
  }
}
```

```json
PUT my-index-000001/_doc/1
{
  "group" : "fans",
  "user" : [
    {
      "first" : "John",
      "last" :  "Smith"
    },
    {
      "first" : "Alice",
      "last" :  "White"
    }
  ]
}
```

This query doesn’t match because `Alice` and `Smith` are not in the same nested object.

```json
GET my-index-000001/_search
{
  "query": {
    "nested": {
      "path": "user",
      "query": {
        "bool": {
          "must": [
            { "match": { "user.first": "Alice" }},
            { "match": { "user.last":  "Smith" }}
          ]
        }
      }
    }
  }
}
```

This query matches because `Alice` and `White` are in the same nested object.
`inner_hits` allow us to highlight the matching nested documents.

```json
GET my-index-000001/_search
{
  "query": {
    "nested": {
      "path": "user",
      "query": {
        "bool": {
          "must": [
            { "match": { "user.first": "Alice" }},
            { "match": { "user.last":  "White" }}
          ]
        }
      },
      "inner_hits": {
        "highlight": {
          "fields": {
            "user.first": {}
          }
        }
      }
    }
  }
}
```

https://opster.com/guides/elasticsearch/data-structuring/elasticsearch-nested-field-object-field/
