package de.adesso.gitchecker.repositorycheck.adapter;

import com.fasterxml.jackson.core.type.TypeReference;
import de.adesso.gitchecker.repositorycheck.domain.BitBucketPagingResponse;
import de.adesso.gitchecker.repositorycheck.domain.BitBucketRepository;
import de.adesso.gitchecker.repositorycheck.domain.PullRequest;
import de.adesso.gitchecker.repositorycheck.port.driven.FindBitBucketPullRequestPort;
import de.adesso.gitchecker.repositorycheck.utils.ExitUtils;
import de.adesso.gitchecker.repositorycheck.utils.WebUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FindBitBucketPullRequestAdapter implements FindBitBucketPullRequestPort {

    @Value("${webclient.requestSize}")
    private Integer requestSize;

    private final WebUtils webUtils;
    private final WebClient client;

    @Override
    public List<PullRequest> byRepository(BitBucketRepository repository) {
        List<PullRequest> pullRequests = new ArrayList<>();
        try {
            Integer firstPagingElement = 0;
            BitBucketPagingResponse pagingResponse;
            do {
                Mono<ClientResponse> response = client.get()
                        .uri(webUtils.getPullRequestURL(repository) + "?limit=" + requestSize + "&start=" + firstPagingElement + "&state=ALL")
                        .header("Authorization", webUtils.getBasicAuthString())
                        .exchange();

                pagingResponse = (BitBucketPagingResponse) webUtils.convertResponse(response, new TypeReference<BitBucketPagingResponse<PullRequest>>(){});
                pullRequests.addAll(pagingResponse.getValues());
                firstPagingElement = pagingResponse.getNextStartPage();
            } while (!pagingResponse.getIsLastPage());
        } catch (Exception e) {
            ExitUtils.pullRequestNotFetched();
        }
        return pullRequests;
    }
}
