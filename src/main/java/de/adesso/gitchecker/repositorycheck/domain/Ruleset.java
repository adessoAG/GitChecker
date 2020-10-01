package de.adesso.gitchecker.repositorycheck.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

@Data
public class Ruleset {

    private Map<String, Pattern> branchNamingPatterns;
    private Map<String, List<String>> allowedBranchOrigins;
    private Map<String, List<String>> allowedBranchMerges;
    private Map<String, Integer> branchStalePeriods;
    private Boolean branchRemovalAfterPRMerge;

    public Set<String> branchTypes() {
        return branchNamingPatterns.keySet();
    }

    @JsonProperty("rules")
    private void unpackFromConfig(Map<String, Object> rules) {
        compileNamingPatterns((Map<String, String>) rules.get("branchNamingPatterns"));
        allowedBranchOrigins = (Map<String, List<String>>) rules.get("allowedBranchOrigins");
        allowedBranchMerges = (Map<String, List<String>>) rules.get("allowedBranchMerges");
        branchStalePeriods = (Map<String, Integer>) rules.get("branchStalePeriods");
        branchRemovalAfterPRMerge = (Boolean) rules.get("branchRemovalAfterPRMerge");
    }

    private void compileNamingPatterns(Map<String, String> patterns) {
        branchNamingPatterns = new HashMap<>();
        patterns.forEach((key, value) -> branchNamingPatterns.put(key, Pattern.compile(value)));
    }
}
