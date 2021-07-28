package com.noboru.webscraping.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GitHubRepositoryInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String extension;
    private Long count;
    private Long lines;
    private Long bytes;

    @ManyToOne
    @JoinColumn(name = "git_hub_repository_id")
    private GitHubRepository repository;

    public GitHubRepositoryInfo(String extension, Long count, Long lines, Long bytes, GitHubRepository repository) {
        this.extension = extension;
        this.count = count;
        this.lines = lines;
        this.bytes = bytes;
        this.repository = repository;
    }
}
