package de.adesso.gitchecker.repositorycheck.port.driven;

import de.adesso.gitchecker.repositorycheck.domain.BitBucketRepository;
import de.adesso.gitchecker.repositorycheck.domain.Branch;

import java.util.Map;

public interface FindBitBucketBranchPort {

    Map<String, Branch> byRepository(BitBucketRepository repository);
}
