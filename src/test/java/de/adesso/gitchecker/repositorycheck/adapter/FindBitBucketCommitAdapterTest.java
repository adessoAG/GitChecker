package de.adesso.gitchecker.repositorycheck.adapter;

import de.adesso.gitchecker.repositorycheck.domain.BitBucketResources;
import de.adesso.gitchecker.repositorycheck.domain.Commit;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
class FindBitBucketCommitAdapterTest {

    @Autowired
    private FindBitBucketCommitAdapter findCommits;
    @Autowired
    private FindBitBucketBranchAdapter findBranches;
    @Autowired
    private BitBucketResources resources;

    @Test
    void commitsBeingFetched() {
        //When
        Map<String, Commit> commits = findCommits.byRepository(resources.getTargetRepository());

        //Then
        Assertions.assertThat(commits.size()).isGreaterThan(0);
    }

    @Test
    void distinctCommitsBeingFetched() {
        //Given
        Object[] commits = findCommits.byRepository(resources.getTargetRepository()).values().toArray();
        Commit from = (Commit) commits[0];
        Commit to = (Commit) commits[commits.length - 1];

        //When
        Map<String, Commit> distinctCommits = findCommits.byDistinction(resources.getTargetRepository(), from, to);

        //Then
        Assertions.assertThat(distinctCommits.size()).isGreaterThan(0);
    }

    @Test
    void allReferencedCommitsBeingFetched() {
        //Given
        resources.getTargetRepository().setBranches(findBranches.byRepository(resources.getTargetRepository()));

        //When
        Map<String, Commit> commits = findCommits.byRepository(resources.getTargetRepository());

        //Then
        long numUnknownCommits = commits.values().stream()
                .mapToLong(commit -> commit.getParentCommits().stream()
                    .filter(parent -> !commits.containsKey(parent.getId())).count())
                .sum();

        Assertions.assertThat(numUnknownCommits).isEqualTo(0);
    }
}
