package de.adesso.gitchecker.repositorycheck.service.check.checks;

import de.adesso.gitchecker.repositorycheck.domain.*;
import de.adesso.gitchecker.repositorycheck.port.driver.CheckRuleUseCase;
import de.adesso.gitchecker.repositorycheck.utils.CheckUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CheckBranchOrigin implements CheckRuleUseCase {

    private final IssueType responsibility = IssueType.ILLEGAL_ORIGIN;
    private final Ruleset ruleset;

    @Override
    public Map<IssueType, List<Issue>> check(BitBucketRepository repository) {
        List<Issue> issues =  repository.getBranches().values().stream()
                .filter(this::isValidBranch)
                .map(this::checkBranchOrigin)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return CheckUtils.issueMap(responsibility, issues);
    }

    private Issue checkBranchOrigin(Branch branch) {
        if (isBranchOriginAllowed(branch)) {
            return null;
        }
        return new Issue(responsibility, "Origin " + branch.getParentBranch().getName() + " of branch " + branch.getName() + " was not allowed.");
    }

    private boolean isValidBranch(Branch branch) {
        return !branch.getBranchType().equalsIgnoreCase("REMOVED")
                && !branch.getBranchType().equalsIgnoreCase("UNKNOWN")
                && !branch.getParentBranch().getBranchType().equalsIgnoreCase("REMOVED")
                && !branch.getParentBranch().getBranchType().equalsIgnoreCase("UNKNOWN");
    }

    private boolean isBranchOriginAllowed(Branch branch) {
        return ruleset.getAllowedBranchOrigins().containsKey(branch.getBranchType())
                && ruleset.getAllowedBranchOrigins().get(branch.getBranchType()).contains(branch.getParentBranch().getBranchType());
    }
}
