package de.adesso.gitchecker.repositorycheck.service.check.checks;

import de.adesso.gitchecker.repositorycheck.domain.*;
import de.adesso.gitchecker.repositorycheck.port.driver.CheckRuleUseCase;
import de.adesso.gitchecker.repositorycheck.utils.CheckUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
public class CheckBranchStalePeriods implements CheckRuleUseCase {

    private final IssueType responsibility = IssueType.BRANCH_STALE;
    private final Ruleset ruleset;

    @Override
    public Map<IssueType, List<Issue>> check(BitBucketRepository repository) {
        if (isCheckRequired()) {
            List<Issue> issues = repository.getBranches().values().stream()
                    .filter(this::isValidBranch)
                    .map(this::checkBranchStale)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            return CheckUtils.issueMap(responsibility, issues);
        }
        return new HashMap<>();
    }

    private Issue checkBranchStale(Branch branch) {
        if (isPeriodSet(branch)) {
            int allowedBranchAge = ruleset.getBranchStalePeriods().get(branch.getBranchType());
            int branchAge = branchAgeDays(branch);

            if (branchAge > allowedBranchAge) {
                return new Issue(responsibility, "Branch " + branch.getName() + " was not updated since " + branchAge + " days.");
            }
        }
        return null;
    }

    private int branchAgeDays(Branch branch) {
        return (int) ChronoUnit.DAYS.between(branch.getLatestCommit().getTimestamp(), LocalDateTime.now());
    }

    private boolean isPeriodSet(Branch branch) {
        return ruleset.getBranchStalePeriods().containsKey(branch.getBranchType());
    }

    private boolean isValidBranch(Branch branch) {
        return !branch.getBranchType().equalsIgnoreCase("REMOVED")
                && !branch.getBranchType().equalsIgnoreCase("UNKNOWN");
    }

    private boolean isCheckRequired() {
        return nonNull(ruleset.getBranchStalePeriods());
    }
}
