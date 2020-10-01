package de.adesso.gitchecker.repositorycheck.port.driver;

import de.adesso.gitchecker.repositorycheck.domain.BitBucketRepository;
import de.adesso.gitchecker.repositorycheck.domain.Branch;

import java.util.Map;

public interface FindBitBucketBranchUseCase {

    Map<String, Branch> byRepository(BitBucketRepository repository);
}
