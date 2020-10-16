package de.adesso.gitchecker.repositorycheck.service.build.find;

import de.adesso.gitchecker.repositorycheck.domain.BitBucketRepository;
import de.adesso.gitchecker.repositorycheck.domain.Branch;
import de.adesso.gitchecker.repositorycheck.domain.Commit;
import de.adesso.gitchecker.repositorycheck.domain.Ruleset;
import de.adesso.gitchecker.repositorycheck.port.driven.FindBitBucketCommitPort;
import de.adesso.gitchecker.repositorycheck.port.driver.FindBitBucketCommitUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class FindBitBucketCommitService implements FindBitBucketCommitUseCase {

    private final FindBitBucketCommitPort findCommits;
    private final Ruleset ruleset;

    @Override
    public Map<String, Commit> byRulesetConfig(BitBucketRepository repository) {
        if (ruleset.areAllCommitsRequired()) {
            return byRepository(repository);
        }

        Map<String, Commit> commits = byBranches(repository);
        if (ruleset.getBranchRemovalAfterPRMerge()) {
            commits.putAll(byPullRequests(repository));
        }
        return commits;
    }

    @Override
    public Map<String, Commit> byRepository(BitBucketRepository repository) {
        return findCommits.byRepository(repository);
    }

    @Override
    public Map<String, Commit> byPullRequests(BitBucketRepository repository) {
        return findCommits.byPullRequests(repository);
    }

    @Override
    public Map<String, Commit> byDistinction(BitBucketRepository repository, Commit from, Commit to) {
        return findCommits.byDistinction(repository, from, to);
    }

    @Override
    public Map<String, Commit> byBranches(BitBucketRepository repository) {
        return findCommits.byBranches(repository);
    }

    @Override
    public Map<String, Commit> byBranch(Branch branch, BitBucketRepository repository) {
        return findCommits.byBranch(branch, repository);
    }
}
