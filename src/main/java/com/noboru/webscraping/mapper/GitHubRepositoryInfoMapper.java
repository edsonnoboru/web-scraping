package com.noboru.webscraping.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.noboru.webscraping.dto.GitHubRepositoryInfoDTO;
import com.noboru.webscraping.model.GitHubRepository;
import com.noboru.webscraping.model.GitHubRepositoryInfo;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class GitHubRepositoryInfoMapper {
    
    public static GitHubRepositoryInfo toEntity(GitHubRepositoryInfoDTO dto, GitHubRepository repository) {
        return new GitHubRepositoryInfo(
            dto.getExtension(),
            dto.getCount(),
            dto.getLines(),
            dto.getBytes(),
            repository);
    }

    public static GitHubRepositoryInfoDTO toDTO(GitHubRepositoryInfo entity) {
        return new GitHubRepositoryInfoDTO(
            entity.getExtension(),
            entity.getCount(),
            entity.getLines(),
            entity.getBytes());
    }

    public static List<GitHubRepositoryInfoDTO> toListDTO(List<GitHubRepositoryInfo> list) {
        return list.stream()
        .map(entity -> GitHubRepositoryInfoMapper.toDTO(entity))
        .collect(Collectors.toList());
    }
    
    public static List<GitHubRepositoryInfo> toListEntity(List<GitHubRepositoryInfoDTO> list, GitHubRepository repository) {
        return list.stream()
        .map(dto -> GitHubRepositoryInfoMapper.toEntity(dto, repository))
        .collect(Collectors.toList());
    }
}
