package com.codecafe.search.redis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.codecafe.search.entity.SearchFilter;

@Repository
public interface SearchFilterRepository extends CrudRepository<SearchFilter, String> {

}