package de.adesso.gitchecker.repositorycheck.service.build.update;

import de.adesso.gitchecker.repositorycheck.adapter.FindBitBucketBranchAdapter;
import de.adesso.gitchecker.repositorycheck.adapter.FindBitBucketCommitAdapter;
import de.adesso.gitchecker.repositorycheck.domain.BitBucketResources;
import de.adesso.gitchecker.repositorycheck.domain.Commit;
import de.adesso.gitchecker.repositorycheck.utils.BitBucketServerDummy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;


@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
class UpdateBranchParentServiceTest {

    @Autowired
    private UpdateBranchParentService branchParents;
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
    void branchParentsBeingUpdated() {
        //Given
        resources.getTargetRepository().setBranches(findBranches.byRepository(resources.getTargetRepository()));
        Map<String, Commit> commits = findCommits.byRepository(resources.getTargetRepository());
        resources.getTargetRepository().setCommits(commits);
        linkCommits.link(resources.getTargetRepository());
        commitCreatorBranches.update(resources.getTargetRepository());

        //When
        branchParents.update(resources.getTargetRepository());

        //Then
        resources.getTargetRepository().getBranches().values().forEach(branch -> {
            System.out.println(branch.getName() + "\n\t " + branch.getParentBranch().getName() + "\n");
        });
    }
}
