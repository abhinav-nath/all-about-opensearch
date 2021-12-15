# Create index

```json
PUT /marvel_movies
{
  "mappings": {
    "properties": {
      "name": {
        "type": "text",
        "fields": {
          "edgengram": {
            "type": "text",
            "analyzer": "edge_ngram_analyzer",
            "search_analyzer": "edge_ngram_search_analyzer"
          },
          "completion": {
            "type": "completion"
          }
        },
        "analyzer": "standard"
      }
    }
  },
  "settings": {
    "analysis": {
      "filter": {
        "edge_ngram_filter": {
          "type": "edge_ngram",
          "min_gram": 1,
          "max_gram": 20
        }
      },
      "analyzer": {
        "edge_ngram_analyzer": {
          "type": "custom",
          "tokenizer": "standard",
          "filter": [
            "lowercase",
            "edge_ngram_filter"
          ]
        },
        "edge_ngram_search_analyzer": {
          "tokenizer": "lowercase"
        }
      }
    }
  }
}
```

* Field `name.edgengram` is analysed using _Edge Ngram tokenizer_, hence it will be used for **Edge Ngram** Approach.

* Field `name.completion` is stored as a _completion type_, hence it will be used for **Completion Suggester**.

# Create documents

```json
POST marvel_movies/_bulk
{"create":{"_id":"1"}}
{"name":"Spider-Man: Homecoming"}
{"create":{"_id":"2"}}
{"name":"Spider-Man: Far From Home"}
{"create":{"_id":"3"}}
{"name":"Spider-Man: No Way Home"}
{"create":{"_id":"4"}}
{"name":"Ant-man and the Wasp"}
{"create":{"_id":"5"}}
{"name":"Avengers: Infinity War"}
{"create":{"_id":"6"}}
{"name":"Avengers: Endgame"}
{"create":{"_id":"7"}}
{"name":"Captain Marvel"}
{"create":{"_id":"8"}}
{"name":"Black Panther"}
{"create":{"_id":"9"}}
{"name":"Guardians of the Galaxy"}
{"create":{"_id":"10"}}
{"name":"Guardians of the Galaxy Vol 2"}
{"create":{"_id":"11"}}
{"name":"Doctor Strange"}
{"create":{"_id":"12"}}
{"name":"Captain America: Civil War"}
{"create":{"_id":"13"}}
{"name":"Ant-Man"}
{"create":{"_id":"14"}}
{"name":"Avengers: Age of Ultron"}
{"create":{"_id":"15"}}
{"name":"Captain America: The Winter Soldier"}
{"create":{"_id":"16"}}
{"name":"Thor"}
{"create":{"_id":"17"}}
{"name":"Thor: The Dark World"}
{"create":{"_id":"18"}}
{"name":"Thor: Ragnarok"}
{"create":{"_id":"19"}}
{"name":"The Incredible Hulk"}
{"create":{"_id":"20"}}
{"name":"The Avengers"}
{"create":{"_id":"21"}}
{"name":"Captain America: The First Avenger"}
{"create":{"_id":"22"}}
{"name":"Iron Man"}
{"create":{"_id":"23"}}
{"name":"The Incredible Hulk"}
{"create":{"_id":"24"}}
{"name":"Iron Man 2"}
{"create":{"_id":"25"}}
{"name":"Iron Man 3"}
{"create":{"_id":"26"}}
{"name":"Black Widow"}
```

# Edge N-gram matching

```json
GET marvel_movies/_search
{
  "query": {
    "match": {
      "name.edgengram": {
        "query": "iro"
      }
    }
  }
}
```

Response

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
      "value" : 3,
      "relation" : "eq"
    },
    "max_score" : 3.1752641,
    "hits" : [
      {
        "_index" : "marvel_movies",
        "_type" : "_doc",
        "_id" : "22",
        "_score" : 3.1752641,
        "_source" : {
          "name" : "Iron Man"
        }
      },
      {
        "_index" : "marvel_movies",
        "_type" : "_doc",
        "_id" : "24",
        "_score" : 3.050744,
        "_source" : {
          "name" : "Iron Man 2"
        }
      },
      {
        "_index" : "marvel_movies",
        "_type" : "_doc",
        "_id" : "25",
        "_score" : 3.050744,
        "_source" : {
          "name" : "Iron Man 3"
        }
      }
    ]
  }
}
```

# Completion Suggester

```json
GET marvel_movies/_search
{
    "suggest": {
        "movie-suggest" : {
            "prefix" : "captain america",
            "completion" : {
                "field" : "name.completion"
            }
        }
    }
}
```

Response

```json
{
  "took" : 4,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 0,
      "relation" : "eq"
    },
    "max_score" : null,
    "hits" : [ ]
  },
  "suggest" : {
    "movie-suggest" : [
      {
        "text" : "captain america",
        "offset" : 0,
        "length" : 15,
        "options" : [
          {
            "text" : "Captain America: Civil War",
            "_index" : "marvel_movies",
            "_type" : "_doc",
            "_id" : "12",
            "_score" : 1.0,
            "_source" : {
              "name" : "Captain America: Civil War"
            }
          },
          {
            "text" : "Captain America: The First Avenger",
            "_index" : "marvel_movies",
            "_type" : "_doc",
            "_id" : "21",
            "_score" : 1.0,
            "_source" : {
              "name" : "Captain America: The First Avenger"
            }
          },
          {
            "text" : "Captain America: The Winter Soldier",
            "_index" : "marvel_movies",
            "_type" : "_doc",
            "_id" : "15",
            "_score" : 1.0,
            "_source" : {
              "name" : "Captain America: The Winter Soldier"
            }
          }
        ]
      }
    ]
  }
}
```

#### We can update the query to include support for fuzziness in the following way

```json
GET marvel_movies/_search
{
  "suggest": {
    "movie-suggest-fuzzy": {
      "prefix": "captain amerca",
      "completion": {
        "field": "name.completion",
        "fuzzy": {
          "fuzziness": 1
        }
      }
    }
  }
}
```

## Auto-suggest tokens instead of whole documents

#### Create Index

```json
PUT marvel_movies
{
  "mappings": {
    "properties": {
      "name": {
        "type": "text"
      },
      "suggest": {
        "type": "completion"
      }
    }
  }
}
```

#### Create documents with suggestions

```json
POST marvel_movies/_bulk
{"create":{"_id":"1"}}
{"name":"Captain America: The First Avenger","suggest":["Captain","America","First","Avenger"]}
{"create":{"_id":"2"}}
{"name":"Captain America: The Winter Soldier","suggest":["Captain","America","Winter","Soldier"]}
{"create":{"_id":"3"}}
{"name":"Iron Man 3","suggest":["Iron Man"]}
```

#### Search query

```json
POST marvel_movies/_search
{
  "_source": false,
  "suggest": {
    "completer": {
      "prefix": "cap",
      "completion": {
        "field": "suggest",
        "skip_duplicates": true
      }
    }
  }
}
```

Response:

```json
{
  "took" : 3,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 0,
      "relation" : "eq"
    },
    "max_score" : null,
    "hits" : [ ]
  },
  "suggest" : {
    "completer" : [
      {
        "text" : "cap",
        "offset" : 0,
        "length" : 3,
        "options" : [
          {
            "text" : "Captain",
            "_index" : "marvel_movies",
            "_type" : "_doc",
            "_id" : "1",
            "_score" : 1.0
          }
        ]
      }
    ]
  }
}
```
