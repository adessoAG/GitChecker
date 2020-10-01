package de.adesso.gitchecker.repositorycheck.adapter;

import de.adesso.gitchecker.repositorycheck.domain.BitBucketResources;
import de.adesso.gitchecker.repositorycheck.domain.Branch;
import org.assertj.core.api.Assertions;
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

    @Test
    void branchesBeingFetched() {
        //When
        Map<String, Branch> branches =  findBranches.byRepository(resources.getTargetRepository());

        //Then
        Assertions.assertThat(branches).containsKey("master");
    }
}
