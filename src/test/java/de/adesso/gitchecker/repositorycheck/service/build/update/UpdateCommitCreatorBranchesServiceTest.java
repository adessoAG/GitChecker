package de.adesso.gitchecker.repositorycheck.service.build.update;

import de.adesso.gitchecker.repositorycheck.adapter.FindBitBucketBranchAdapter;
import de.adesso.gitchecker.repositorycheck.adapter.FindBitBucketCommitAdapter;
import de.adesso.gitchecker.repositorycheck.domain.BitBucketResources;
import de.adesso.gitchecker.repositorycheck.domain.Branch;
import de.adesso.gitchecker.repositorycheck.domain.Commit;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
class UpdateCommitCreatorBranchesServiceTest {

    @Autowired
    private UpdateCommitCreatorBranchesService commitCreatorBranches;
    @Autowired
    private LinkCommitsService linkCommits;
    @Autowired
    private FindBitBucketCommitAdapter findCommits;
    @Autowired
    private FindBitBucketBranchAdapter findBranches;
    @Autowired
    private BitBucketResources resources;

    @Test
    void allCommitBeingAssignedToBranch() {
        //Given
        resources.getTargetRepository().setBranches(findBranches.byRepository(resources.getTargetRepository()));
        Map<String, Commit> commits = findCommits.byRepository(resources.getTargetRepository());
        resources.getTargetRepository().setCommits(commits);
        linkCommits.link(resources.getTargetRepository());

        //When
        commitCreatorBranches.update(resources.getTargetRepository());

        //Then
        long numCommitsWithoutBranch = commits.values().stream()
                .filter(commit -> commit.getCreatorBranch() == null)
                .count();

        commits.values().stream()
                .filter(commit -> commit.getCreatorBranch() == null)
                .forEach(commit -> System.out.println(commit.getId().substring(0, 6) + " " + commit.getMessage().replace("\n", " ")));

        Assertions.assertThat(numCommitsWithoutBranch).isEqualTo(0);
    }

    @Test
    void existingBranchesBeingAssignedCorrectly() {
        //Given
        resources.getTargetRepository().setBranches(findBranches.byRepository(resources.getTargetRepository()));
        Map<String, Commit> commits = findCommits.byRepository(resources.getTargetRepository());
        resources.getTargetRepository().setCommits(commits);
        linkCommits.link(resources.getTargetRepository());
        Map<String, Commit> branchPointCommits = resources.getTargetRepository().getBranchPointCommits();

        //When
        commitCreatorBranches.update(resources.getTargetRepository());

        //Then
        long wrongAssignedCommits = resources.getTargetRepository().getBranches().values().stream()
                .mapToLong(branch -> branchCommitChain(branch, branchPointCommits).stream()
                        .filter(commit -> !commit.getCreatorBranch().equals(branch))
                        .count())
                .sum();

        resources.getTargetRepository().getBranches().values().forEach(branch ->
                        branchCommitChain(branch, branchPointCommits).stream()
                                .filter(commit -> !commit.getCreatorBranch().equals(branch))
                                .forEach(parent -> System.out.println(parent.getAuthorName() + " " + parent.getId().substring(0, 5) + " " + parent.getCreatorBranch().getName() + " " + parent.getMessage().replace("\n", " "))));

        Assertions.assertThat(wrongAssignedCommits).isEqualTo(0);
    }

    private List<Commit> branchCommitChain(Branch branch, Map<String, Commit> branchPointCommits) {
        List<Commit> branchCommits = new ArrayList<>();
        Commit commit = branch.getLatestCommit();
        while (commit != null && !branchPointCommits.containsKey(commit.getId())) {
            branchCommits.add(commit);
            commit = commit.getParentCommits().size() == 0 ? null : commit.getParentCommits().get(0);
        }
        return branchCommits;
    }

    @Test
    void printBranchFromCommit() {
        //Given
        String fromCommitId = "";
        resources.getTargetRepository().setBranches(findBranches.byRepository(resources.getTargetRepository()));
        Map<String, Commit> commits = findCommits.byRepository(resources.getTargetRepository());
        resources.getTargetRepository().setCommits(commits);
        linkCommits.link(resources.getTargetRepository());

        //When
        commitCreatorBranches.update(resources.getTargetRepository());

        //Then
        resources.getTargetRepository().getCommits().values().stream()
                .filter(commit -> commit.getId().startsWith(fromCommitId))
                .forEach(commit -> {
                    while (Objects.nonNull(commit)) {
                        System.out.println(commit.getId().substring(0, 6) + " " + commit.getCreatorBranch().getBranchType() + " " + commit.getCreatorBranch().getName() + " " + commit.getMessage().replace("\n", " "));
                        commit = commit.getParentCommits().get(0);

                    }
                });
    }
}
