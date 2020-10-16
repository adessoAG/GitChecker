package de.adesso.gitchecker.repositorycheck.port.driver;

import de.adesso.gitchecker.repositorycheck.domain.BitBucketRepository;
import de.adesso.gitchecker.repositorycheck.domain.Branch;
import de.adesso.gitchecker.repositorycheck.domain.Commit;

import java.util.Map;

public interface FindBitBucketCommitUseCase {

    Map<String, Commit> byRulesetConfig(BitBucketRepository repository);
    Map<String, Commit> byRepository(BitBucketRepository repository);
    Map<String, Commit> byPullRequests(BitBucketRepository repository);
    Map<String, Commit> byDistinction(BitBucketRepository repository, Commit from, Commit to);
    Map<String, Commit> byBranches(BitBucketRepository branches);
    Map<String, Commit> byBranch(Branch branch, BitBucketRepository branches);
}
