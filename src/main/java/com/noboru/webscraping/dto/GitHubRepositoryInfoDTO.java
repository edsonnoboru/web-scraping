package com.noboru.webscraping.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GitHubRepositoryInfoDTO {
    private String extension;
    private Long count;
    private Long lines;
    private Long bytes;
}
