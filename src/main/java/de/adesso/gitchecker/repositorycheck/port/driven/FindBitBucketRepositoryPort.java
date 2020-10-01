package de.adesso.gitchecker.repositorycheck.port.driven;

import de.adesso.gitchecker.repositorycheck.domain.BitBucketProject;
import de.adesso.gitchecker.repositorycheck.domain.BitBucketRepository;

import java.util.List;

public interface FindBitBucketRepositoryPort {

    List<BitBucketRepository> byProject(BitBucketProject project);
    BitBucketRepository byProjectAndSlug(BitBucketProject project, String repositorySlug);
}
