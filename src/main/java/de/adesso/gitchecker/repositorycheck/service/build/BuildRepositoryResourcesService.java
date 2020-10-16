package de.adesso.gitchecker.repositorycheck.service.build;

import de.adesso.gitchecker.repositorycheck.domain.BitBucketProject;
import de.adesso.gitchecker.repositorycheck.domain.BitBucketRepository;
import de.adesso.gitchecker.repositorycheck.domain.BitBucketResources;
import de.adesso.gitchecker.repositorycheck.domain.Ruleset;
import de.adesso.gitchecker.repositorycheck.port.driver.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BuildRepositoryResourcesService implements BuildRepositoryResourcesUseCase {

    private final FindBitBucketProjectUseCase findProjects;
    private final FindBitBucketRepositoryUseCase findRepositories;
    private final FindBitBucketBranchUseCase findBranches;
    private final FindBitBucketPullRequestUseCase findPullRequests;
    private final FindBitBucketCommitUseCase findCommits;
    private final LinkCommitsUseCase linkCommits;
    private final UpdateCommitCreatorBranchesUseCase commitCreatorBranches;
    private final UpdateBranchMergesUseCase branchMerges;
    private final UpdateBranchParentUseCase branchParents;
    private final UpdateCommitDiffCounterUseCase commitDiffCounters;
    private final Ruleset ruleset;

    @Override
    public void build(BitBucketResources resources) {
        BitBucketRepository repository = resources.getTargetRepository();
        BitBucketProject project = resources.getProject();

        project.copyValues(findProjects.byProjectKey(project.getKey()));
        repository.copyValues(findRepositories.byProjectAndSlug(project, repository.getSlug()));

        repository.setBranches(findBranches.byRepository(repository));
        if (ruleset.getBranchRemovalAfterPRMerge()) {
            repository.setPullRequests(findPullRequests.byRepository(repository));
        }
        repository.setCommits(findCommits.byRulesetConfig(repository));

        linkCommits.link(repository);
        commitCreatorBranches.update(repository);
        branchMerges.update(repository);
        branchParents.update(repository);
        commitDiffCounters.update(repository);
    }
}
