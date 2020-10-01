package de.adesso.gitchecker.repositorycheck.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BitBucketRepository {

    private Integer id;
    private String slug;
    private String name;
    private String description;
    @JsonIgnore
    private BitBucketProject project;
    private Map<String, Commit> commits = new HashMap<>();
    private Map<String, Commit> branchPointCommits = new HashMap<>();
    private Map<String, Commit> mergeCommits = new HashMap<>();
    private Map<String, Branch> branches = new HashMap<>();
    private List<PullRequest> pullRequests = new ArrayList<>();

    public void copyValues(BitBucketRepository repository) {
        id = repository.getId();
        slug = repository.getSlug();
        name = repository.getName();
        description = repository.getDescription();
    }
}
