package de.adesso.gitchecker.repositorycheck.service.build.find;

import de.adesso.gitchecker.repositorycheck.domain.BitBucketRepository;
import de.adesso.gitchecker.repositorycheck.domain.Commit;
import de.adesso.gitchecker.repositorycheck.port.driven.FindBitBucketCommitPort;
import de.adesso.gitchecker.repositorycheck.port.driver.FindBitBucketCommitUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class FindBitBucketCommitService implements FindBitBucketCommitUseCase {

    private final FindBitBucketCommitPort findCommits;

    @Override
    public Map<String, Commit> byRepository(BitBucketRepository repository) {
        return findCommits.byRepository(repository);
    }

    @Override
    public Map<String, Commit> byDistinction(BitBucketRepository repository, Commit from, Commit to) {
        return findCommits.byDistinction(repository, from, to);
    }
}
