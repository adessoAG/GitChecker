package de.adesso.gitchecker.repositorycheck.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Branch {

    private String id;
    @JsonProperty("displayId")
    private String name;
    private Boolean isDefault;
    private Integer commitsBeforeDefault;
    private Integer commitsBehindDefault;
    private String branchType;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Commit latestCommit;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Branch parentBranch;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Branch> mergedInto = new HashSet<>();

    @JsonProperty("latestCommit")
    private void initLatestCommit(String id) {
        this.latestCommit = new Commit();
        latestCommit.setId(id);
    }
}
