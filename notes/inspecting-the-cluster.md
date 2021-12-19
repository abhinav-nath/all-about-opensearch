# Inspecting the cluster

## Get cluster health

```
GET /_cluster/health
```

```
{
  "cluster_name" : "opensearch-cluster",
  "status" : "green",
  "timed_out" : false,
  "number_of_nodes" : 2,
  "number_of_data_nodes" : 2,
  "discovered_master" : true,
  "active_primary_shards" : 44,
  "active_shards" : 88,
  "relocating_shards" : 0,
  "initializing_shards" : 0,
  "unassigned_shards" : 0,
  "delayed_unassigned_shards" : 0,
  "number_of_pending_tasks" : 0,
  "number_of_in_flight_fetch" : 0,
  "task_max_waiting_in_queue_millis" : 0,
  "active_shards_percent_as_number" : 100.0
}
```

## Get details of nodes

```
GET /_cat/nodes?v
```

```
ip         heap.percent ram.percent cpu load_1m load_5m load_15m node.role master name
172.19.0.3           58          99  13    0.49    0.29     0.16 dimr      -      opensearch-node1
172.19.0.4           67          99  13    0.49    0.29     0.16 dimr      *      opensearch-node2
```

## Get details of indices

```
GET /_cat/indices?v
```

```
health status index                                       uuid                   pri rep docs.count docs.deleted store.size pri.store.size
green  open   marvel_movies                               5c9TL9xsRIepH-wFHp_tBw   1   1          3            0     11.2kb          5.6kb
green  open   products-index                              oN8hBNiXTcCgkxZDE7-cRw   1   1          2            0     92.7kb         46.3kb
```
