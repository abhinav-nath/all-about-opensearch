# Object field type

```json
PUT my-index-000001/_doc/1
{
  "region": "US",
  "manager": {
    "age":     30,
    "name": {
      "first": "John",
      "last":  "Smith"
    }
  }
}
```

- The outer document is also a JSON object.
- It contains an inner object called `manager`.
- Which in turn contains an inner object called `name`.

Internally, this document is indexed as a simple, flat list of key-value pairs, something like this:

```json
{
  "region":             "US",
  "manager.age":        30,
  "manager.name.first": "John",
  "manager.name.last":  "Smith"
}
```

An explicit mapping for the above document could look like this:

```json
PUT my-index-000001
{
  "mappings": {
    "properties": {
      "region": {
        "type": "keyword"
      },
      "manager": {
        "properties": {
          "age":  { "type": "integer" },
          "name": {
            "properties": {
              "first": { "type": "text" },
              "last":  { "type": "text" }
            }
          }
        }
      }
    }
  }
}
```


- Properties in the top-level mappings definition.
- The `manager` field is an inner `object` field.
- The `manager.name` field is an inner object field within the `manager` field.

> You are not required to set the field `type` to `object` explicitly, as this is the default value.

Source:
https://www.elastic.co/guide/en/elasticsearch/reference/current/object.html
