package de.adesso.gitchecker.repositorycheck.port.driven;

import de.adesso.gitchecker.repositorycheck.domain.BitBucketProject;

import java.util.List;

public interface FindBitBucketProjectPort {

    List<BitBucketProject> findAll();
    BitBucketProject byProjectKey(String projectKey);
}
