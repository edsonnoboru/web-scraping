package com.noboru.webscraping.repository;

import com.noboru.webscraping.model.GitHubRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GitHubRepositoryRepository extends JpaRepository<GitHubRepository, Long>{
    GitHubRepository findByUrl(String url);
}
