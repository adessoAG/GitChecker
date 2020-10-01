package de.adesso.gitchecker.repositorycheck.service.build.find;

import de.adesso.gitchecker.repositorycheck.domain.BitBucketProject;
import de.adesso.gitchecker.repositorycheck.domain.BitBucketRepository;
import de.adesso.gitchecker.repositorycheck.port.driven.FindBitBucketRepositoryPort;
import de.adesso.gitchecker.repositorycheck.port.driver.FindBitBucketRepositoryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FindBitBucketRepositoryService implements FindBitBucketRepositoryUseCase {

    private final FindBitBucketRepositoryPort findRepositories;

    @Override
    public List<BitBucketRepository> byProject(BitBucketProject project) {
        return findRepositories.byProject(project);
    }

    @Override
    public BitBucketRepository byProjectAndSlug(BitBucketProject project, String repositorySlug) {
        return findRepositories.byProjectAndSlug(project, repositorySlug);
    }
}
