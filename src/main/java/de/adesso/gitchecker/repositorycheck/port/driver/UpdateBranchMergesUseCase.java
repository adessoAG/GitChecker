package de.adesso.gitchecker.repositorycheck.port.driver;

import de.adesso.gitchecker.repositorycheck.domain.BitBucketRepository;

public interface UpdateBranchMergesUseCase {

    void update(BitBucketRepository repository);
}
