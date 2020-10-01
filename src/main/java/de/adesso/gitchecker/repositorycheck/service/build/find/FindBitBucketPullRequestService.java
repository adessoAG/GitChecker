package de.adesso.gitchecker.repositorycheck.service.build.find;

import de.adesso.gitchecker.repositorycheck.domain.BitBucketRepository;
import de.adesso.gitchecker.repositorycheck.domain.PullRequest;
import de.adesso.gitchecker.repositorycheck.port.driven.FindBitBucketPullRequestPort;
import de.adesso.gitchecker.repositorycheck.port.driver.FindBitBucketPullRequestUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FindBitBucketPullRequestService implements FindBitBucketPullRequestUseCase {

    private final FindBitBucketPullRequestPort findPullRequests;

    @Override
    public List<PullRequest> byRepository(BitBucketRepository repository) {
        return findPullRequests.byRepository(repository);
    }
}
