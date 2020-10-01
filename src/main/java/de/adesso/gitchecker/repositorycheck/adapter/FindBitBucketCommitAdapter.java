package de.adesso.gitchecker.repositorycheck.adapter;

import com.fasterxml.jackson.core.type.TypeReference;
import de.adesso.gitchecker.repositorycheck.domain.BitBucketPagingResponse;
import de.adesso.gitchecker.repositorycheck.domain.BitBucketRepository;
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

    @Value("${webclient.requestSize}")
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
}
