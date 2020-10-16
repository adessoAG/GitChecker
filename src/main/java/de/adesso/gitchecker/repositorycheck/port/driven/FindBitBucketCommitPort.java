package de.adesso.gitchecker.repositorycheck.port.driven;

import de.adesso.gitchecker.repositorycheck.domain.BitBucketRepository;
import de.adesso.gitchecker.repositorycheck.domain.Branch;
import de.adesso.gitchecker.repositorycheck.domain.Commit;

import java.util.Map;

public interface FindBitBucketCommitPort {

    Map<String, Commit> byRepository(BitBucketRepository repository);
    Map<String, Commit> byDistinction(BitBucketRepository repository, Commit from, Commit to);
    Map<String, Commit> byBranches(BitBucketRepository repository);
    Map<String, Commit> byBranch(Branch branch, BitBucketRepository repository);
    Map<String, Commit> byPullRequests(BitBucketRepository repository);
}
