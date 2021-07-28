package com.noboru.webscraping.controller;

import java.util.List;

import com.noboru.webscraping.dto.GitHubRepositoryInfoDTO;
import com.noboru.webscraping.service.GitHubRepositoryInfoService;
import com.noboru.webscraping.shared.exception.ErrorException;
import com.noboru.webscraping.util.exception.WebScrapingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/git-hub")
public class GitHubController {

    @Autowired
    private GitHubRepositoryInfoService gitHubRepositoryInfoService;

    @RequestMapping(value="/repository/info", method=RequestMethod.GET)
    public List<GitHubRepositoryInfoDTO> findByUrl(@RequestParam String url) throws WebScrapingException, ErrorException {
        return gitHubRepositoryInfoService.findAllByUrl(url);
    }

}
