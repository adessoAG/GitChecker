package de.adesso.gitchecker.repositorycheck.service.build.update;

import de.adesso.gitchecker.repositorycheck.domain.BitBucketRepository;
import de.adesso.gitchecker.repositorycheck.domain.Branch;
import de.adesso.gitchecker.repositorycheck.domain.Commit;
import de.adesso.gitchecker.repositorycheck.port.driver.UpdateBranchParentUseCase;
import org.springframework.stereotype.Service;

@Service
public class UpdateBranchParentService implements UpdateBranchParentUseCase {

    @Override
    public void update(BitBucketRepository repository) {
        assignBranchParentsUsingBranchingPoints(repository);
        assignParentOfBranchesWithoutCommits(repository);
    }

    private void assignBranchParentsUsingBranchingPoints(BitBucketRepository repository) {
        repository.getBranchPointCommits().values().forEach(this::assignParentsOfNewBranches);
    }

    private void assignParentsOfNewBranches(Commit branchingPointCommit) {
        branchingPointCommit.getChildCommits().forEach(child -> {

            if (!isSameBranch(branchingPointCommit, child)) {
                assignParentOfBranch(branchingPointCommit, child);
            }
        });
    }

    private void assignParentOfBranchesWithoutCommits(BitBucketRepository repository) {
        repository.getBranches().values().forEach(branch -> {
            if (isBranchWithoutCommit(branch)) {
                assignCreatorOfReferencedCommitAsParent(branch);
            }
        });
    }

    private void assignCreatorOfReferencedCommitAsParent(Branch branch) {
        branch.setParentBranch(branch.getLatestCommit().getCreatorBranch());
    }

    private boolean isBranchWithoutCommit(Branch branch) {
        return branch.getParentBranch() == null && !branch.getLatestCommit().getCreatorBranch().equals(branch);
    }

    private boolean isSameBranch(Commit commit, Commit commit2) {
        return commit.getCreatorBranch().equals(commit2.getCreatorBranch());
    }

    private void assignParentOfBranch(Commit parent, Commit child) {
        child.getCreatorBranch().setParentBranch(parent.getCreatorBranch());
    }
}
