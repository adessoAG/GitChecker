package de.adesso.gitchecker.repositorycheck.service.check;

import de.adesso.gitchecker.repositorycheck.domain.BitBucketResources;
import de.adesso.gitchecker.repositorycheck.port.driver.CheckRuleUseCase;
import de.adesso.gitchecker.repositorycheck.port.driver.IdentifyIssuesUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IdentifyIssuesService implements IdentifyIssuesUseCase {

    private final List<CheckRuleUseCase> rules;

    @Override
    public void identify(BitBucketResources resources) {
        rules.stream()
                .map(rule -> rule.check(resources.getTargetRepository()))
                .forEach(resources.getRepositoryIssues()::putAll);
    }
}
