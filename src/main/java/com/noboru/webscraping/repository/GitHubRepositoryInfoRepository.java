package com.noboru.webscraping.repository;

import com.noboru.webscraping.model.GitHubRepositoryInfo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GitHubRepositoryInfoRepository extends JpaRepository<GitHubRepositoryInfo, Long>{
    
}
