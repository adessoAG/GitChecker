package de.adesso.gitchecker.repositorycheck.service.build.find;

import de.adesso.gitchecker.repositorycheck.domain.BitBucketProject;
import de.adesso.gitchecker.repositorycheck.port.driven.FindBitBucketProjectPort;
import de.adesso.gitchecker.repositorycheck.port.driver.FindBitBucketProjectUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FindBitBucketProjectService implements FindBitBucketProjectUseCase {

    private final FindBitBucketProjectPort findProject;

    @Override
    public List<BitBucketProject> findAll() {
        return findProject.findAll();
    }

    @Override
    public BitBucketProject byProjectKey(String projectKey) {
        return findProject.byProjectKey(projectKey);
    }
}
