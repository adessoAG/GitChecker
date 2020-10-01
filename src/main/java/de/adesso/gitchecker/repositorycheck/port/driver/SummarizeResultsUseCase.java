package de.adesso.gitchecker.repositorycheck.port.driver;

import de.adesso.gitchecker.repositorycheck.domain.BitBucketResources;

public interface SummarizeResultsUseCase {

    void summarize(BitBucketResources resources);
}
