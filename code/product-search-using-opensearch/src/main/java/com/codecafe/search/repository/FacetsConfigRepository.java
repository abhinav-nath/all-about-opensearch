package com.codecafe.search.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codecafe.search.entity.FacetsConfig;

public interface FacetsConfigRepository extends JpaRepository<FacetsConfig, String> {

}