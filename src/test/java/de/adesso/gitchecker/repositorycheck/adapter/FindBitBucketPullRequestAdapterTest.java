package de.adesso.gitchecker.repositorycheck.adapter;

import de.adesso.gitchecker.repositorycheck.domain.BitBucketResources;
import de.adesso.gitchecker.repositorycheck.domain.PullRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
class FindBitBucketPullRequestAdapterTest {

    @Autowired
    private FindBitBucketPullRequestAdapter findPullRequests;
    @Autowired
    private BitBucketResources resources;

    @Test
    void pullRequestsBeingFetched() {
        //When
        List<PullRequest> pullRequests = findPullRequests.byRepository(resources.getTargetRepository());

        //Then
        Assertions.assertThat(pullRequests.size()).isGreaterThan(0);
    }
}
