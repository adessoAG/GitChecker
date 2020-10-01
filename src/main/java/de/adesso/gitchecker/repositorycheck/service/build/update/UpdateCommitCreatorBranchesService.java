package de.adesso.gitchecker.repositorycheck.service.build.update;

import de.adesso.gitchecker.repositorycheck.domain.BitBucketRepository;
import de.adesso.gitchecker.repositorycheck.domain.Branch;
import de.adesso.gitchecker.repositorycheck.domain.Commit;
import de.adesso.gitchecker.repositorycheck.port.driver.UpdateCommitCreatorBranchesUseCase;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UpdateCommitCreatorBranchesService implements UpdateCommitCreatorBranchesUseCase {

    @Override
    public void update(BitBucketRepository repository) {
        updateDefaultBranchCommits(repository);
        updateMergedBranches(repository);
        updateUnmergedBranches(repository);
    }

    private void updateDefaultBranchCommits(BitBucketRepository repository) {
        Branch defaultBranch = getDefaultBranch(repository);

        Commit nextCommit = defaultBranch.getLatestCommit();
        nextCommit.setCreatorBranch(defaultBranch);

        while (nextCommit.getParentCommits().size() > 0) {
            nextCommit = nextCommit.getParentCommits().get(0);
            nextCommit.setCreatorBranch(defaultBranch);
        }
    }

    private void updateMergedBranches(BitBucketRepository repository) {
        repository.getMergeCommits().values().stream()
                .sorted(Comparator.comparing(Commit::getTimestamp))
                .forEach(mergeCommit -> {
                    Branch creatorBranch = branchOfCommitChainEnd(mergeCommit, repository);
                    List<Commit> branchCommits = commitChainFromCommit(mergeCommit.getParentCommits().get(1));
                    assignBranchToCommits(creatorBranch, branchCommits);
                });
    }

    private void updateUnmergedBranches(BitBucketRepository repository) {
        repository.getBranches().values().stream()
                .filter(branch -> hasNoCreatorBranch(branch.getLatestCommit()))
                .forEach(branch -> {
                    List<Commit> branchCommits = commitChainFromCommit(branch.getLatestCommit());
                    assignBranchToCommits(branch, branchCommits);
                });
    }

    private List<Commit> commitChainFromCommit(Commit commit) {
        List<Commit> commits = new ArrayList<>();
        while (hasNoCreatorBranch(commit)) {
            commits.add(commit);
            commit = commit.getParentCommits().get(0);
        }
        return commits;
    }

    private boolean hasNoCreatorBranch(Commit commit) {
        return Objects.nonNull(commit) && Objects.isNull(commit.getCreatorBranch());
    }

    private Branch branchOfCommitChainEnd(Commit mergeCommit, BitBucketRepository repository) {
        Branch branch = branchPointingToCommit(mergeCommit.getParentCommits().get(1), repository);
        if (Objects.isNull(branch)) {
            branch = branchPointingToCommit(mergeCommit, repository);
        }
        if (Objects.isNull(branch)) {
            branch = removedBranch();
        }
        return branch;
    }

    private void assignBranchToCommits(Branch creator, List<Commit> branchCommits) {
        branchCommits.stream().forEach(commit -> commit.setCreatorBranch(creator));
    }

    private Branch branchPointingToCommit(Commit commit, BitBucketRepository repository) {
        List<Branch> branchList = repository.getBranches().values().stream()
                .filter(branch -> branch.getLatestCommit().equals(commit))
                .collect(Collectors.toList());

        return branchList.size() == 0 ? null : branchList.get(0);
    }

    private Branch removedBranch() {
        Branch removedBranch = new Branch();
        removedBranch.setBranchType("REMOVED");
        return removedBranch;
    }

    private Branch getDefaultBranch(BitBucketRepository repository) {
        return repository.getBranches().values().stream()
                .filter(Branch::getIsDefault)
                .collect(Collectors.toList()).get(0);
    }
}
