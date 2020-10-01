package de.adesso.gitchecker.repositorycheck.port.driver;

import de.adesso.gitchecker.repositorycheck.domain.BitBucketRepository;
import de.adesso.gitchecker.repositorycheck.domain.PullRequest;

import java.util.List;

public interface FindBitBucketPullRequestUseCase {

    List<PullRequest> byRepository(BitBucketRepository repository);
}
