package de.adesso.gitchecker.repositorycheck.port.driver;

import de.adesso.gitchecker.repositorycheck.domain.BitBucketResources;

public interface IdentifyIssuesUseCase {

    void identify(BitBucketResources resources);
}
