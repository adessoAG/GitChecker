package de.adesso.gitchecker.repositorycheck.port.driver;

import de.adesso.gitchecker.repositorycheck.domain.BitBucketRepository;
import de.adesso.gitchecker.repositorycheck.domain.Commit;

import java.util.Map;

public interface FindBitBucketCommitUseCase {

    Map<String, Commit> byRepository(BitBucketRepository repository);
    Map<String, Commit> byDistinction(BitBucketRepository repository, Commit from, Commit to);
}
