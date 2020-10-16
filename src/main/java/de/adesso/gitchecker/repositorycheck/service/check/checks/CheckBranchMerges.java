package de.adesso.gitchecker.repositorycheck.service.check.checks;

import de.adesso.gitchecker.repositorycheck.domain.*;
import de.adesso.gitchecker.repositorycheck.port.driver.CheckRuleUseCase;
import de.adesso.gitchecker.repositorycheck.utils.CheckUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
public class CheckBranchMerges implements CheckRuleUseCase {

    private final IssueType responsibility = IssueType.ILLEGAL_MERGE;
    private final Ruleset ruleset;

    @Override
    public Map<IssueType, List<Issue>> check(BitBucketRepository repository) {
        if (isCheckRequired()) {
            List<Issue> issues = repository.getBranches().values().stream()
                    .filter(this::isValidBranch)
                    .map(this::checkBranchMerges)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());

            return CheckUtils.issueMap(responsibility, issues);
        }
        return new HashMap<>();
    }

    private List<Issue> checkBranchMerges(Branch branch) {
        return branch.getMergedInto().stream()
                .filter(this::isValidBranch)
                .filter(mergedInto -> isIllegalMerge(branch, mergedInto))
                .map(illegalMergeTarget -> new Issue(responsibility, "Merge of " + branch.getName() + " into " + illegalMergeTarget.getName() + " was not allowed."))
                .collect(Collectors.toList());
    }

    private boolean isIllegalMerge(Branch from, Branch to) {
        return isDifferentBranch(from, to) && (isBranchMergeUnchecked(from) ||
                !(isMergeTargetDeclared(from, to) || (canMergeIntoOrigin(from) && mergesIntoOrigin(from, to))));
    }

    private boolean isValidBranch(Branch branch) {
        return !branch.getBranchType().equalsIgnoreCase("REMOVED")
                && !branch.getBranchType().equalsIgnoreCase("UNKNOWN");
    }

    private boolean isBranchMergeUnchecked(Branch from) {
        return !ruleset.getAllowedBranchMerges().containsKey(from.getBranchType());
    }

    private boolean isMergeTargetDeclared(Branch from, Branch to) {
        return ruleset.getAllowedBranchMerges().get(from.getBranchType()).contains(to.getBranchType());
    }

    private boolean canMergeIntoOrigin(Branch from) {
        return ruleset.getAllowedBranchMerges().get(from.getBranchType()).contains("ORIGIN");
    }

    private boolean mergesIntoOrigin(Branch from, Branch to) {
        return from.getParentBranch().equals(to);
    }

    private boolean isDifferentBranch(Branch from, Branch to) {
        return !from.equals(to);
    }

    private boolean isCheckRequired() {
        return nonNull(ruleset.getAllowedBranchMerges());
    }
}
