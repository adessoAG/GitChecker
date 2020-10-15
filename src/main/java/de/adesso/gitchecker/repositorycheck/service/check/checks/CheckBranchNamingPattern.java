package de.adesso.gitchecker.repositorycheck.service.check.checks;

import de.adesso.gitchecker.repositorycheck.domain.*;
import de.adesso.gitchecker.repositorycheck.port.driver.CheckRuleUseCase;
import de.adesso.gitchecker.repositorycheck.utils.CheckUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CheckBranchNamingPattern implements CheckRuleUseCase {

    private final IssueType responsibility = IssueType.ILLEGAL_NAMING;
    private final Ruleset ruleset;

    @Override
    public Map<IssueType, List<Issue>> check(BitBucketRepository repository) {
        List<Issue> issues =  repository.getBranches().values().stream()
                .map(this::checkBranchName)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return CheckUtils.issueMap(responsibility, issues);
    }

    private Issue checkBranchName(Branch branch) {
        for (Map.Entry<String, Pattern> entry : ruleset.getBranchNamingPatterns().entrySet()) {
             if (branchNameMatchesPattern(branch, entry.getValue())) {
                branch.setBranchType(entry.getKey());
                return null;
            }
        }
        branch.setBranchType("UNKNOWN");
        return new Issue(responsibility, "No pattern matches branch: " + branch.getName());
    }

    private boolean branchNameMatchesPattern(Branch branch, Pattern pattern) {
        return pattern.matcher(branch.getName()).find();
    }
}
