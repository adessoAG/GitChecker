package de.adesso.gitchecker.repositorycheck.adapter;

import de.adesso.gitchecker.repositorycheck.domain.BitBucketResources;
import de.adesso.gitchecker.repositorycheck.domain.Branch;
import de.adesso.gitchecker.repositorycheck.utils.BitBucketServerDummy;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
class FindBitBucketBranchAdapterTest {

    @Autowired
    private FindBitBucketBranchAdapter findBranches;
    @Autowired
    private BitBucketResources resources;
    @Autowired
    private BitBucketServerDummy serverDummy;

    @BeforeEach
    public void setUp() {
        serverDummy.init();
    }

    @AfterEach
    public void shutDown() {
        serverDummy.kill();
    }

    @Test
    void branchesBeingFetched() {
        //When
        Map<String, Branch> branches = findBranches.byRepository(resources.getTargetRepository());

        //Then
        Assertions.assertThat(branches).isNotEmpty();
    }
}
