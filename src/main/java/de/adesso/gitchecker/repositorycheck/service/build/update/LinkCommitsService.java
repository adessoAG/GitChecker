package de.adesso.gitchecker.repositorycheck.service.build.update;

import de.adesso.gitchecker.repositorycheck.domain.BitBucketRepository;
import de.adesso.gitchecker.repositorycheck.domain.Commit;
import de.adesso.gitchecker.repositorycheck.domain.PullRequest;
import de.adesso.gitchecker.repositorycheck.port.driver.LinkCommitsUseCase;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LinkCommitsService implements LinkCommitsUseCase {

    @Override
    public void link(BitBucketRepository repository) {
        linkCommitParents(repository);
        linkCommitChildren(repository);
        linkBranchCommits(repository);
        linkPullRequestCommits(repository);
        updateBranchingPointCommits(repository);
        updateMergeCommits(repository);
    }

    private void linkCommitParents(BitBucketRepository repository) {
        repository.getCommits().values()
                .forEach(commit -> commit.setParentCommits(getParents(commit, repository.getCommits())));
    }

    private void linkCommitChildren(BitBucketRepository repository) {
        repository.getCommits().values()
                .forEach(commit -> commit.setChildCommits(getChildren(commit, repository.getCommits())));
    }

    private ArrayList<Commit> getParents(Commit commit, Map<String, Commit> commits) {
        ArrayList<Commit> parents = new ArrayList<>();
        commit.getParentCommits().forEach(parent -> {
            if (commits.containsKey(parent.getId())) {
                parents.add(commits.get(parent.getId()));
            }
        });
        return parents;
    }

    private List<Commit> getChildren(Commit parent, Map<String, Commit> commits) {
        return commits.values().stream()
                .filter(commit -> commit.getParentCommits().contains(parent))
                .collect(Collectors.toList());
    }

    private void linkBranchCommits(BitBucketRepository repository) {
        repository.getBranches().values()
                .forEach(branch -> branch.setLatestCommit(repository.getCommits().get(branch.getLatestCommit().getId())));
    }

    private void linkPullRequestCommits(BitBucketRepository repository) {
        repository.getPullRequests().forEach(pr -> {
            if (arePRCommitsInRepo(pr, repository)) {
                pr.setFrom(repository.getCommits().get(pr.getFrom().getId()));
                pr.setTo(repository.getCommits().get(pr.getTo().getId()));
            }
        });
    }

    private boolean arePRCommitsInRepo(PullRequest pr, BitBucketRepository repository) {
        return repository.getCommits().containsKey(pr.getFrom())
                && repository.getCommits().containsKey(pr.getTo());
    }

    private void updateBranchingPointCommits(BitBucketRepository repository) {
        repository.getCommits().values().stream()
                .filter(Commit::isBranchPointCommit)
                .forEach(branchPointCommit -> repository.getBranchPointCommits().put(branchPointCommit.getId(), branchPointCommit));
    }

    private void updateMergeCommits(BitBucketRepository repository) {
        repository.getCommits().values().stream()
                .filter(Commit::isMergeCommit)
                .forEach(mergeCommit -> repository.getMergeCommits().put(mergeCommit.getId(), mergeCommit));
    }
}
