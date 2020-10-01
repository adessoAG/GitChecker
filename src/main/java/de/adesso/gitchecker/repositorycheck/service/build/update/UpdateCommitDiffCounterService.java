package de.adesso.gitchecker.repositorycheck.service.build.update;

import de.adesso.gitchecker.repositorycheck.domain.BitBucketRepository;
import de.adesso.gitchecker.repositorycheck.domain.Branch;
import de.adesso.gitchecker.repositorycheck.port.driver.FindBitBucketCommitUseCase;
import de.adesso.gitchecker.repositorycheck.port.driver.UpdateCommitDiffCounterUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UpdateCommitDiffCounterService implements UpdateCommitDiffCounterUseCase {

    private final FindBitBucketCommitUseCase findCommits;

    @Override
    public void update(BitBucketRepository repository) {
        repository.getBranches().values().stream()
                .filter(branch -> !branch.getIsDefault())
                .forEach(branch -> updateBranchCommitDiffCounters(branch, repository));
    }

    private void updateBranchCommitDiffCounters(Branch branch, BitBucketRepository repository) {
        Branch defaultBranch = getDefaultBranch(repository);
        updateCommitsBeforeDefaultBranch(branch, defaultBranch, repository);
        updateCommitsBehindDefaultBranch(branch, defaultBranch, repository);
    }

    private void updateCommitsBeforeDefaultBranch(Branch branch, Branch defaultBranch, BitBucketRepository repository) {
        int numBefore = findCommits.byDistinction(repository, branch.getLatestCommit(), defaultBranch.getLatestCommit()).size();
        branch.setCommitsBeforeDefault(numBefore);
    }

    private void updateCommitsBehindDefaultBranch(Branch branch, Branch defaultBranch, BitBucketRepository repository) {
        int numBehind = findCommits.byDistinction(repository, defaultBranch.getLatestCommit(), branch.getLatestCommit()).size();
        branch.setCommitsBehindDefault(numBehind);
    }

    private Branch getDefaultBranch(BitBucketRepository repository) {
        return repository.getBranches().values().stream()
                .filter(Branch::getIsDefault)
                .collect(Collectors.toList())
                .get(0);
    }
}
