package de.adesso.gitchecker.repositorycheck.utils;

import de.adesso.gitchecker.repositorycheck.domain.Issue;
import de.adesso.gitchecker.repositorycheck.domain.IssueType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckUtils {

    public static Map<IssueType, List<Issue>> issueMap(IssueType type, List<Issue> issues) {
        Map<IssueType, List<Issue>> resultMap = new HashMap<>();
        resultMap.put(type, issues);
        return resultMap;
    }
}
