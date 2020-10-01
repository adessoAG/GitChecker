package de.adesso.gitchecker.repositorycheck.service.build.find;

import de.adesso.gitchecker.repositorycheck.domain.BitBucketRepository;
import de.adesso.gitchecker.repositorycheck.domain.Branch;
import de.adesso.gitchecker.repositorycheck.port.driven.FindBitBucketBranchPort;
import de.adesso.gitchecker.repositorycheck.port.driver.FindBitBucketBranchUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class FindBitBucketBranchService implements FindBitBucketBranchUseCase {

    private final FindBitBucketBranchPort findBranches;

    @Override
    public Map<String, Branch> byRepository(BitBucketRepository repository) {
        return findBranches.byRepository(repository);
    }
}
