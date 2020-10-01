package de.adesso.gitchecker.repositorycheck.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BitBucketProject {

    private Integer id;
    private String key;
    private String name;
    private String description;
    private List<BitBucketRepository> repositories = new ArrayList<>();

    public void copyValues(BitBucketProject project) {
        id = project.getId();
        key = project.getKey();
        name = project.getName();
        description = project.getDescription();
        repositories = project.getRepositories();
    }
}
