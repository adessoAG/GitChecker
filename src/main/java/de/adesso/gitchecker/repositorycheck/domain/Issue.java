package de.adesso.gitchecker.repositorycheck.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Issue {

    private IssueType type;
    private String description;

    @Override
    public String toString() {
        return description;
    }
}
