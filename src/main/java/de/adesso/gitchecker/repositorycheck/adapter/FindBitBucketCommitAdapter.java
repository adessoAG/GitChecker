package de.adesso.gitchecker.repositorycheck.adapter;

import com.fasterxml.jackson.core.type.TypeReference;
import de.adesso.gitchecker.repositorycheck.domain.BitBucketPagingResponse;
import de.adesso.gitchecker.repositorycheck.domain.BitBucketRepository;
import de.adesso.gitchecker.repositorycheck.domain.Branch;
import de.adesso.gitchecker.repositorycheck.domain.Commit;
import de.adesso.gitchecker.repositorycheck.port.driven.FindBitBucketCommitPort;
import de.adesso.gitchecker.repositorycheck.utils.ExitUtils;
import de.adesso.gitchecker.repositorycheck.utils.WebUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;

@Component
@RequiredArgsConstructor
public class FindBitBucketCommitAdapter implements FindBitBucketCommitPort {

    @Value("${webclient.requestSize.commits}")
    private Integer requestSize;

    private final WebUtils webUtils;
    private final WebClient client;

    @Override
    public Map<String, Commit> byRepository(BitBucketRepository repository) {
        Map<String, Commit> commits = new HashMap<>();
        try {
            Integer firstPagingElement = 0;
            BitBucketPagingResponse pagingResponse;
            do {
                Mono<ClientResponse> response = client.get()
                        .uri(webUtils.getCommitURL(repository) + "?limit=" + requestSize + "&start=" + firstPagingElement)
                        .header("Authorization", webUtils.getBasicAuthString())
                        .exchange();

                pagingResponse = (BitBucketPagingResponse) webUtils.convertResponse(response, new TypeReference<BitBucketPagingResponse<Commit>>() {});
                ((List<Commit>) pagingResponse.getValues()).forEach(commit -> commits.put(commit.getId(), commit));
                firstPagingElement = pagingResponse.getNextStartPage();
            } while (!pagingResponse.getIsLastPage());
            examineUnknownBranchReferences(repository, commits);
        } catch (Exception e) {
            ExitUtils.commitsNotFetched();
        }
        return commits;
    }

    private void examineUnknownBranchReferences(BitBucketRepository repository, Map<String, Commit> commits) {
        Queue<Commit> commitsToCheck = new LinkedList<>();
        repository.getBranches().values().forEach(branch -> commitsToCheck.add(branch.getLatestCommit()));
        while (!commitsToCheck.isEmpty()) {
            Commit commit = commitsToCheck.poll();
            if (!commits.containsKey(commit.getId())) {
                commit = byId(repository, commit.getId());
                commits.put(commit.getId(), commit);
                commitsToCheck.addAll(commit.getParentCommits());
            }
        }
    }

    @Override
    public Map<String, Commit> byDistinction(BitBucketRepository repository, Commit from, Commit to) {
        Map<String, Commit> commits = new HashMap<>();
        try {
            Integer firstPagingElement = 0;
            BitBucketPagingResponse pagingResponse;
            do {
                Mono<ClientResponse> response = client.get()
                        .uri(webUtils.getDistinctCommitURL(repository) + "?limit=" + requestSize + "&start=" + firstPagingElement + "&from=" + from.getId() + "&to=" + to.getId())
                        .header("Authorization", webUtils.getBasicAuthString())
                        .exchange();

                pagingResponse = (BitBucketPagingResponse) webUtils.convertResponse(response, new TypeReference<BitBucketPagingResponse<Commit>>() {});
                ((List<Commit>) pagingResponse.getValues()).forEach(commit -> commits.put(commit.getId(), commit));
                firstPagingElement = pagingResponse.getNextStartPage();
            } while (!pagingResponse.getIsLastPage());
        } catch (Exception e) {
            ExitUtils.branchesNotFetched();
        }
        return commits;
    }

    @Override
    public Map<String, Commit> byBranches(BitBucketRepository repository) {
        Map<String, Commit> commits = new HashMap<>();
        repository.getBranches().values().forEach(branch ->
                byBranch(branch, commits, repository)
        );
        return commits;
    }

    @Override
    public Map<String, Commit> byBranch(Branch branch, BitBucketRepository repository) {
        Map<String, Commit> commits = new HashMap<>();
        return byBranch(branch, commits, repository);
    }

    @Override
    public Map<String, Commit> byPullRequests(BitBucketRepository repository) {
        Map<String, Commit> commits = new HashMap<>();
        repository.getPullRequests().forEach(pr -> {
            commits.put(pr.getFrom().getId(), byId(repository, pr.getFrom().getId()));
            commits.put(pr.getTo().getId(), byId(repository, pr.getTo().getId()));
        });
        return commits;
    }

    private Map<String, Commit> byBranch(Branch branch, Map<String, Commit> commits, BitBucketRepository repository) {
        Commit commit = branch.getLatestCommit();
        commit = getFromMapOrFetch(commit, commits, repository);
        while (commit.getParentCommits().size() > 0) {
            commit = commit.getParentCommits().get(0);
            commit = getFromMapOrFetch(commit, commits, repository);
        }
        return commits;
    }

    private Commit byId(BitBucketRepository repository, String commitId) {
        try {
            Mono<ClientResponse> response = client.get()
                    .uri(webUtils.getCommitURL(repository) + commitId)
                    .header("Authorization", webUtils.getBasicAuthString())
                    .exchange();

            return (Commit) webUtils.convertResponse(response, new TypeReference<Commit>() {
            });
        } catch (Exception e) {
            ExitUtils.commitsNotFetched();
        }
        return null;
    }

    private Commit getFromMapOrFetch(Commit commit, Map<String, Commit> commits, BitBucketRepository repository) {
        if (commits.containsKey(commit.getId())) {
            return commits.get(commit.getId());
        }
        commit = byId(repository, commit.getId());
        commits.put(commit.getId(), commit);
        return commit;
    }
}
