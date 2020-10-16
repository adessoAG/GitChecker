package de.adesso.gitchecker.repositorycheck.service.check.checks;

import de.adesso.gitchecker.repositorycheck.domain.*;
import de.adesso.gitchecker.repositorycheck.port.driver.CheckRuleUseCase;
import de.adesso.gitchecker.repositorycheck.utils.CheckUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CheckBranchRemovalAfterPRMerge implements CheckRuleUseCase {

    private final IssueType responsibility = IssueType.BRANCH_UNREMOVED;
    private final Ruleset ruleset;

    @Override
    public Map<IssueType, List<Issue>> check(BitBucketRepository repository) {
        if (ruleset.getBranchRemovalAfterPRMerge()) {
            List<Issue> issues = repository.getBranches().values().stream()
                    .filter(this::isValidBranch)
                    .map(branch -> checkBranchRemovalAfterPRMerges(branch, repository))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            return CheckUtils.issueMap(responsibility, issues);
        }
        return new HashMap<>();
    }

    private Issue checkBranchRemovalAfterPRMerges(Branch branch, BitBucketRepository repository) {
        List<PullRequest> pullRequests = pullRequestsFromBranch(branch, repository);
        if (pullRequests.size() > 0) {
            long requestsOpen = pullRequests.stream().filter(PullRequest::getIsOpen).count();
            if (requestsOpen < 1) {
                return new Issue(responsibility, "Branch " + branch.getName() + " was not removed after pull request merge.");
            }
        }
        return null;
    }

    private List<PullRequest> pullRequestsFromBranch(Branch branch, BitBucketRepository repository) {
        return repository.getPullRequests().stream()
                .filter(pr -> Objects.nonNull(pr.getFrom()) && Objects.nonNull(pr.getTo()))
                .filter(pr -> pr.getFrom().equals(branch.getLatestCommit()))
                .collect(Collectors.toList());
    }

    private boolean isValidBranch(Branch branch) {
        return !branch.getBranchType().equalsIgnoreCase("REMOVED");
    }
}
