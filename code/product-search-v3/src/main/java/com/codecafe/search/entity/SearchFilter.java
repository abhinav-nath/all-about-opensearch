package com.codecafe.search.entity;

import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@RedisHash("Student")
public class Student implements Serializable {

  private String searchFilter;
  private String searchField;

}