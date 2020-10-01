package de.adesso.gitchecker.repositorycheck.port.driver;

import de.adesso.gitchecker.repositorycheck.domain.BitBucketProject;

import java.util.List;

public interface FindBitBucketProjectUseCase {

    List<BitBucketProject> findAll();
    BitBucketProject byProjectKey(String projectKey);
}
