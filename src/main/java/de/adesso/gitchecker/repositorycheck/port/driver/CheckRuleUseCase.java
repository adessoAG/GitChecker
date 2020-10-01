package de.adesso.gitchecker.repositorycheck.port.driver;

import de.adesso.gitchecker.repositorycheck.domain.BitBucketRepository;
import de.adesso.gitchecker.repositorycheck.domain.Issue;
import de.adesso.gitchecker.repositorycheck.domain.IssueType;

import java.util.List;
import java.util.Map;

public interface CheckRuleUseCase {

    Map<IssueType, List<Issue>> check(BitBucketRepository repository);
}
