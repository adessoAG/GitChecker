package de.adesso.gitchecker.repositorycheck.service.build.update;

import de.adesso.gitchecker.repositorycheck.adapter.FindBitBucketBranchAdapter;
import de.adesso.gitchecker.repositorycheck.adapter.FindBitBucketCommitAdapter;
import de.adesso.gitchecker.repositorycheck.domain.BitBucketResources;
import de.adesso.gitchecker.repositorycheck.domain.Commit;
import de.adesso.gitchecker.repositorycheck.utils.BitBucketServerDummy;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
class LinkCommitsServiceTest {

    @Autowired
    private LinkCommitsService linkCommits;
    @Autowired
    private FindBitBucketCommitAdapter findCommits;
    @Autowired
    private FindBitBucketBranchAdapter findBranches;
    @Autowired
    private BitBucketResources resources;
    @Autowired
    private BitBucketServerDummy serverDummy;

    @BeforeEach
    void setUp() {
        serverDummy.init();
    }

    @AfterEach
    void shutDown() {
        serverDummy.kill();
    }

    @Test
    void allParentsBeingLinked() {
        //Given
        Map<String, Commit> commits = findCommits.byRepository(resources.getTargetRepository());
        resources.getTargetRepository().setCommits(commits);

        //When
        linkCommits.link(resources.getTargetRepository());

        //Then
        long numCommitsWithoutLinkedParents = commits.values().stream()
                .filter(commit -> commit.getParentCommits().stream()
                        .anyMatch(parent -> parent.getAuthorName().isEmpty()))
                .count();

        Assertions.assertThat(numCommitsWithoutLinkedParents).isEqualTo(0);
    }

    @Test
    void branchCommitsBeingLinked() {
        //Given
        resources.getTargetRepository().setBranches(findBranches.byRepository(resources.getTargetRepository()));
        Map<String, Commit> commits = findCommits.byRepository(resources.getTargetRepository());
        resources.getTargetRepository().setCommits(commits);

        //When
        linkCommits.link(resources.getTargetRepository());

        //Then
        resources.getTargetRepository().getBranches().values()
                .forEach(branch -> Assertions.assertThat(branch.getLatestCommit().getMessage()).isNotEmpty());
    }

    @Test
    void commitParentsStillOrdered() {
        //Given
        Map<String, Commit> commits = findCommits.byRepository(resources.getTargetRepository());
        resources.getTargetRepository().setCommits(commits);
        Map<String, ArrayList<Commit>> before = getCommitsWithMultipleParents(commits);

        //When
        linkCommits.link(resources.getTargetRepository());

        //Then
        Map<String, ArrayList<Commit>> after = getCommitsWithMultipleParents(commits);
        before.forEach((id, parentsBefore) -> {
            ArrayList<Commit> parentsAfter = after.get(id);
            for (int i = 0; i < parentsBefore.size(); i++) {
                Assertions.assertThat(parentsBefore.get(i).getId()).isEqualTo(parentsAfter.get(i).getId());
            }
        });
    }

    private Map<String, ArrayList<Commit>> getCommitsWithMultipleParents(Map<String, Commit> commits) {
        return commits.values().stream()
                .filter(commit -> commit.getParentCommits().size() > 1)
                .collect(Collectors.toMap(Commit::getId, Commit::getParentCommits));
    }
}
