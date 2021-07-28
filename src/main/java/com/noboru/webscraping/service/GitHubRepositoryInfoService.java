package com.noboru.webscraping.service;

import java.util.List;

import com.noboru.webscraping.dto.GitHubRepositoryInfoDTO;
import com.noboru.webscraping.shared.exception.ErrorException;
import com.noboru.webscraping.util.exception.WebScrapingException;

public interface GitHubRepositoryInfoService {
    /**
     * Find all information of repository for URL and save in database all information to next request
     * @param url of GitHub Repository 
     * @return list of information
     * @throws WebScrapingException
     * @throws ErrorException
     */
    List<GitHubRepositoryInfoDTO> findAllByUrl(String url) throws WebScrapingException, ErrorException;
}
