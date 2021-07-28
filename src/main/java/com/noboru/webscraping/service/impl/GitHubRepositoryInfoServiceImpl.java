package com.noboru.webscraping.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.noboru.webscraping.dto.GitHubRepositoryInfoDTO;
import com.noboru.webscraping.mapper.GitHubRepositoryInfoMapper;
import com.noboru.webscraping.model.GitHubRepository;
import com.noboru.webscraping.repository.GitHubRepositoryInfoRepository;
import com.noboru.webscraping.repository.GitHubRepositoryRepository;
import com.noboru.webscraping.service.GitHubRepositoryInfoService;
import com.noboru.webscraping.shared.exception.ErrorException;
import com.noboru.webscraping.util.WebScrapingUtil;
import com.noboru.webscraping.util.exception.WebScrapingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GitHubRepositoryInfoServiceImpl implements GitHubRepositoryInfoService{

    private static final String BASE_URL_GIT_HUB = "https://github.com";
    private static final String PATTERN_ELEMENT_DIRECTORY = "<div role=\"row\" class=\"Box-row Box-row--focus-gray py-2 d-flex position-relative js-navigation-item \">          <div role=\"gridcell\" class=\"mr-3 flex-shrink-0\" style=\"width: 16px;\">              <svg aria-label=\"Directory\" aria-hidden=\"true\" viewBox=\"0 0 16 16\" version=\"1.1\" data-view-component=\"true\" height=\"16\" width=\"16\" class=\"octicon octicon-file-directory hx_color-icon-directory\">    <path fill-rule=\"evenodd\" d=\"M1.75 1A1.75 1.75 0 000 2.75v10.5C0 14.216.784 15 1.75 15h12.5A1.75 1.75 0 0016 13.25v-8.5A1.75 1.75 0 0014.25 3h-6.5a.25.25 0 01-.2-.1l-.9-1.2c-.33-.44-.85-.7-1.4-.7h-3.5z\"></path></svg>          </div>          <div role=\"rowheader\" class=\"flex-auto min-width-0 col-md-2 mr-3\">            <span class=\"css-truncate css-truncate-target d-block width-fit\"><a class=\"js-navigation-open Link--primary\" title=\"([^\"]*)\" data-pjax=\"#repo-content-pjax-container\" href=\"([^\"]*)\">";
    private static final int NUM_GROUP_PATTERN_DIRECTORY = 2;
    private static final String PATTERN_ELEMENT_FILE = "<div role=\"row\" class=\"Box-row Box-row--focus-gray py-2 d-flex position-relative js-navigation-item \">          <div role=\"gridcell\" class=\"mr-3 flex-shrink-0\" style=\"width: 16px;\">              <svg aria-label=\"File\" aria-hidden=\"true\" viewBox=\"0 0 16 16\" version=\"1.1\" data-view-component=\"true\" height=\"16\" width=\"16\" class=\"octicon octicon-file color-icon-tertiary\">    <path fill-rule=\"evenodd\" d=\"M3.75 1.5a.25.25 0 00-.25.25v11.5c0 .138.112.25.25.25h8.5a.25.25 0 00.25-.25V6H9.75A1.75 1.75 0 018 4.25V1.5H3.75zm5.75.56v2.19c0 .138.112.25.25.25h2.19L9.5 2.06zM2 1.75C2 .784 2.784 0 3.75 0h5.086c.464 0 .909.184 1.237.513l3.414 3.414c.329.328.513.773.513 1.237v8.086A1.75 1.75 0 0112.25 15h-8.5A1.75 1.75 0 012 13.25V1.75z\"></path></svg>          </div>          <div role=\"rowheader\" class=\"flex-auto min-width-0 col-md-2 mr-3\">            <span class=\"css-truncate css-truncate-target d-block width-fit\"><a class=\"js-navigation-open Link--primary\" title=\"([^\"]*)\" data-pjax=\"#repo-content-pjax-container\" href=\"([^\"]*)\">";
    private static final int NUM_GROUP_PATTERN_FILE = 2;

    private static final String PATTERN_ELEMENT_NAME_FILE = "<strong class=\"final-path\">([^\"]*)</strong>";
    private static final int NUM_GROUP_PATTERN_NAME_FILE = 1;
    private static final String PATTERN_ELEMENT_INFO_FILE = ">([^\"]*)<span class=\"file-info-divider\"></span>([^\"]*)</div>";
    private static final int NUM_GROUP_PATTERN_LINES_FILE = 1;
    private static final int NUM_GROUP_PATTERN_SIZE_FILE = 2;

    private static final String PATTERN_ELEMENT_VIEW_RAW = "<div class=\"text-center p-3\">          <a href=\"([^\"]*)\">View raw</a>";
    private static final String PATTERN_ELEMENT_ONLY_SIZE_FILE = "<div    class=\"Box-header py-2 pr-2 d-flex flex-shrink-0 flex-md-row flex-items-center\"      >  <div class=\"text-mono f6 flex-auto pr-3 flex-order-2 flex-md-order-1\">([^\"]*)</div>";
    private static final int NUM_GROUP_PATTERN_ONLY_SIZE_FILE = 1;

    private static final String PATTERN_ELEMENT_LAST_UPDATE = "<relative-time datetime=\"([^\"]*)\" class=\"no-wrap\">([^\"]*)</relative-time>";
    private static final int NUM_GROUP_PATTERN_LAST_UPDATE = 1;

    @Autowired
    private GitHubRepositoryRepository gitHubRepositoryRepository;

    @Autowired
    private GitHubRepositoryInfoRepository gitHubRepositoryInfoRepository;

    @Override
    public List<GitHubRepositoryInfoDTO> findAllByUrl(String url) throws WebScrapingException, ErrorException {
        if(WebScrapingUtil.isURLInvalid(url)) {
            throw new ErrorException("Invalid repository URL: " + url);
        }
        LocalDateTime moreUpdated = getLastUpdateTime(url);
        GitHubRepository repository = gitHubRepositoryRepository.findByUrl(url);
        List<GitHubRepositoryInfoDTO> listDTO = new ArrayList<>();        

        if(Objects.nonNull(repository) && repository.getLastUpdateTime().equals(moreUpdated)) {
            listDTO.addAll(GitHubRepositoryInfoMapper.toListDTO(repository.getGitHubRepositoryInfos()));
        } else {
            Map<String, GitHubRepositoryInfoDTO> mapExtensionInfo = new HashMap<>();
            captureCalculateGitHubRepositoryInfo(mapExtensionInfo, url);
            listDTO = mapExtensionInfo.values().stream().collect(Collectors.toList());

            GitHubRepository newRepository = new GitHubRepository();
            newRepository.setUrl(url);
            newRepository.setLastUpdateTime(moreUpdated);
            newRepository = gitHubRepositoryRepository.save(newRepository);
            GitHubRepositoryInfoMapper.toListEntity(listDTO, newRepository)
                                      .stream()
                                      .forEach(info -> gitHubRepositoryInfoRepository.save(info));
        }
        return listDTO;
    }

    private void captureCalculateGitHubRepositoryInfo(Map<String, GitHubRepositoryInfoDTO> mapExtensionInfo, String url) 
            throws ErrorException, WebScrapingException {

        String html = getHTML(url);
        List<String> directoryUrls = WebScrapingUtil.getListGroupContentFromPattern(PATTERN_ELEMENT_DIRECTORY, NUM_GROUP_PATTERN_DIRECTORY, html);
        List<String> fileUrls = WebScrapingUtil.getListGroupContentFromPattern(PATTERN_ELEMENT_FILE, NUM_GROUP_PATTERN_FILE, html);

        // directoryUrls.forEach((directoryUrl) -> {
        //     try {
        //         captureCalculateGitHubRepositoryInfo(mapExtensionInfo, BASE_URL_GIT_HUB + directoryUrl);
        //     } catch (ErrorException | WebScrapingException e) {
        //         throw new ErrorException("There was a problem capturing and calculating repository information!");
        //     }
        // });
        
        fileUrls.forEach((fileUrl) -> {
            try {
                String fileHtml = WebScrapingUtil.getHTML(BASE_URL_GIT_HUB + fileUrl);
                System.out.println("FILE: " + fileUrl);
                String stringFileName = WebScrapingUtil.getContentFromPattern(PATTERN_ELEMENT_NAME_FILE, NUM_GROUP_PATTERN_NAME_FILE, fileHtml);
                String stringExtension = getExtension(stringFileName);

                Long longNumberLines;
                
                Boolean isFileWithoutNumberLines = isFileWithViewRaw(fileHtml);
                if(isFileWithoutNumberLines) {
                    longNumberLines = 0L;
                } else {
                    String stringNumLines = WebScrapingUtil.getContentFromPattern(PATTERN_ELEMENT_INFO_FILE, NUM_GROUP_PATTERN_LINES_FILE, fileHtml);
                    longNumberLines = getNumberLines(stringNumLines);
                }

                String stringSize = WebScrapingUtil.getContentFromPattern(
                                                                    isFileWithoutNumberLines ? PATTERN_ELEMENT_ONLY_SIZE_FILE : PATTERN_ELEMENT_INFO_FILE,
                                                                    isFileWithoutNumberLines ? NUM_GROUP_PATTERN_ONLY_SIZE_FILE : NUM_GROUP_PATTERN_SIZE_FILE,
                                                                    fileHtml);
                Long longSizeBytes = getSizeInBytes(stringSize);
              
                GitHubRepositoryInfoDTO item = mapExtensionInfo.get(stringExtension);
                if(Objects.nonNull(item)) {
                    item.setCount(item.getCount() + 1L);
                    item.setLines(item.getLines() + longNumberLines);
                    item.setBytes(item.getBytes() + longSizeBytes);
                } else {
                    item = new GitHubRepositoryInfoDTO(stringExtension, 1L, longNumberLines, longSizeBytes);
                }
                mapExtensionInfo.put(stringExtension, item);
            } catch (WebScrapingException e) {
                throw new ErrorException(e.getMessage());
            }
        });
    }

    private String getExtension(String stringFileName) {
        String extension;
        if(stringFileName.contains(".")) {
            String[] arrayStrings = stringFileName.split("\\.");
            if(arrayStrings.length == 1) {
                extension = stringFileName.substring(1);
            } else {   
                extension = arrayStrings[arrayStrings.length - 1];
            }
        } else {
            extension = "";
        }
        return extension;
    }

    private Long getNumberLines(String stringWithNumberLines) {
        int indexOfPattern = stringWithNumberLines.indexOf(" lines");
        return Long.valueOf(stringWithNumberLines.substring(0, indexOfPattern).replaceAll(" ", "").replaceAll("</span>", ""));
    }

    private Long getSizeInBytes(String stringWithSize) {
        Long sizeInBytes;
        String stringSizeFormated = stringWithSize.replaceAll(" ", "");
        if(stringSizeFormated.endsWith("Bytes")) {
            sizeInBytes = Long.valueOf(stringSizeFormated.replace("Bytes", ""));
        } else if (stringSizeFormated.endsWith("KB")) {
            sizeInBytes = (long) (1024 * Double.valueOf(stringSizeFormated.replace("KB", "")));
        } else if (stringSizeFormated.endsWith("MB")) {
            sizeInBytes = (long) (1024 * 1024 * Double.valueOf(stringSizeFormated.replace("MB", "")));
        } else {
            sizeInBytes = 0L;
        }
        return sizeInBytes;
    }
    
    private String getHTML(String url) throws ErrorException, WebScrapingException {
        if(WebScrapingUtil.isURLInvalid(url)) {
            throw new ErrorException("Url: \"" + url + "\" informed is invalid");
        }
        return WebScrapingUtil.getHTML(url);
    }

    private Boolean isFileWithViewRaw(String html) {
        return Pattern.compile(PATTERN_ELEMENT_VIEW_RAW).matcher(html).find();
    }

    private LocalDateTime getLastUpdateTime(String url) throws ErrorException, WebScrapingException {
        String html = getHTML(url);
        String stringLastUpdateTime = WebScrapingUtil.getContentFromPattern(PATTERN_ELEMENT_LAST_UPDATE, NUM_GROUP_PATTERN_LAST_UPDATE, html);
        return LocalDateTime.parse(stringLastUpdateTime.substring(0, stringLastUpdateTime.length() - 1)); 
    }
}
