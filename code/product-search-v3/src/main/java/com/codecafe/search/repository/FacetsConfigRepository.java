package com.codecafe.search.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codecafe.search.entity.FacetsConfig;

@Repository
public interface FacetsConfigRepository extends JpaRepository<FacetsConfig, String> {

}