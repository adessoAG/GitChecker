package de.adesso.gitchecker.repositorycheck.service.check;

import de.adesso.gitchecker.repositorycheck.domain.BitBucketResources;
import de.adesso.gitchecker.repositorycheck.domain.Issue;
import de.adesso.gitchecker.repositorycheck.domain.IssueType;
import de.adesso.gitchecker.repositorycheck.port.driver.SummarizeResultsUseCase;
import de.adesso.gitchecker.repositorycheck.utils.ExitUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class SummarizeResultsService implements SummarizeResultsUseCase {

    @Override
    public void summarize(BitBucketResources resources) {
        if (issueCount(resources) < 1) {
            printRepositoryOk();
            return;
        }
        ExitUtils.issuesDiscovered();
        printIssues(resources.getRepositoryIssues());
    }

    private void printIssues(Map<IssueType, List<Issue>> issueMap) {
        System.out.println();
        issueMap.forEach((type, issues) -> {
            if (issues.size() > 0) {
                System.out.println(type);
                issues.forEach(System.out::println);
                System.out.println();
            }
        });
    }

    private void printRepositoryOk() {
        System.out.println("\nNo issues found in repository.\n");
    }

    long issueCount(BitBucketResources resources) {
        return resources.getRepositoryIssues().values().stream()
                .flatMap(Collection::stream)
                .count();
    }
}
