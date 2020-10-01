package de.adesso.gitchecker.repositorycheck.service;

import de.adesso.gitchecker.repositorycheck.domain.BitBucketResources;
import de.adesso.gitchecker.repositorycheck.port.driver.BuildRepositoryResourcesUseCase;
import de.adesso.gitchecker.repositorycheck.port.driver.IdentifyIssuesUseCase;
import de.adesso.gitchecker.repositorycheck.port.driver.PerformRepositoryCheckUseCase;
import de.adesso.gitchecker.repositorycheck.port.driver.SummarizeResultsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PerformRepositoryCheckService implements PerformRepositoryCheckUseCase {

    private final BitBucketResources resources;
    private final BuildRepositoryResourcesUseCase buildRepository;
    private final IdentifyIssuesUseCase identifyIssues;
    private final SummarizeResultsUseCase summarizeResults;

    @Override
    public void perform() {
        buildRepository.build(resources);
        identifyIssues.identify(resources);
        summarizeResults.summarize(resources);
    }
}
