package de.adesso.gitchecker.repositorycheck.service.build.update;

import de.adesso.gitchecker.repositorycheck.adapter.FindBitBucketBranchAdapter;
import de.adesso.gitchecker.repositorycheck.adapter.FindBitBucketCommitAdapter;
import de.adesso.gitchecker.repositorycheck.domain.BitBucketResources;
import de.adesso.gitchecker.repositorycheck.domain.Commit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
class UpdateBranchMergesServiceTest {

    @Autowired
    private UpdateBranchMergesService branchMerges;
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
    void branchMergesBeingUpdated() {
        //Given
        resources.getTargetRepository().setBranches(findBranches.byRepository(resources.getTargetRepository()));
        Map<String, Commit> commits = findCommits.byRepository(resources.getTargetRepository());
        resources.getTargetRepository().setCommits(commits);
        linkCommits.link(resources.getTargetRepository());
        commitCreatorBranches.update(resources.getTargetRepository());

        //When
        branchMerges.update(resources.getTargetRepository());

        //Then
        resources.getTargetRepository().getBranches().values().forEach(branch -> {
            System.out.println(branch.getName());
            branch.getMergedInto().forEach(mergedInto -> System.out.println("\t" + mergedInto.getName()));
            System.out.println();
        });
    }

}
