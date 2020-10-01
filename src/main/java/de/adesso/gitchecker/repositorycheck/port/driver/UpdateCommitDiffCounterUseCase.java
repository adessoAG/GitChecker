package de.adesso.gitchecker.repositorycheck.port.driver;

import de.adesso.gitchecker.repositorycheck.domain.BitBucketRepository;

public interface UpdateCommitDiffCounterUseCase {

    void update(BitBucketRepository repository);
}
