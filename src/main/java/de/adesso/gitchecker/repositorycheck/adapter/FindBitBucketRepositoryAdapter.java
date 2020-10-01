package de.adesso.gitchecker.repositorycheck.adapter;

import com.fasterxml.jackson.core.type.TypeReference;
import de.adesso.gitchecker.repositorycheck.domain.BitBucketPagingResponse;
import de.adesso.gitchecker.repositorycheck.domain.BitBucketProject;
import de.adesso.gitchecker.repositorycheck.domain.BitBucketRepository;
import de.adesso.gitchecker.repositorycheck.port.driven.FindBitBucketRepositoryPort;
import de.adesso.gitchecker.repositorycheck.utils.ExitUtils;
import de.adesso.gitchecker.repositorycheck.utils.WebUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FindBitBucketRepositoryAdapter implements FindBitBucketRepositoryPort {

    @Value("${webclient.requestSize}")
    private Integer requestSize;

    private final WebUtils webUtils;
    private final WebClient client;

    @Override
    public List<BitBucketRepository> byProject(BitBucketProject project) {
        List<BitBucketRepository> repositories = new ArrayList<>();
        try {
            Integer firstPagingElement = 0;
            BitBucketPagingResponse pagingResponse;
            do {
                Mono<ClientResponse> response = client.get()
                        .uri(webUtils.getRepositoryURL(project.getKey()) + "?limit=" + requestSize + "&start=" + firstPagingElement)
                        .header("Authorization", webUtils.getBasicAuthString())
                        .exchange();

                pagingResponse = (BitBucketPagingResponse) webUtils.convertResponse(response, new TypeReference<BitBucketPagingResponse<BitBucketRepository>>(){});
                repositories.addAll(pagingResponse.getValues());
                firstPagingElement = pagingResponse.getNextStartPage();
            } while (!pagingResponse.getIsLastPage());
        } catch (Exception e) {
            ExitUtils.repositoryNotFetched();
        }
        return repositories;
    }

    @Override
    public BitBucketRepository byProjectAndSlug(BitBucketProject project, String repositorySlug) {
        BitBucketRepository repository = null;
        try {
            Integer firstPagingElement = 0;
            BitBucketPagingResponse pagingResponse;

                Mono<ClientResponse> response = client.get()
                        .uri(webUtils.getRepositoryURL(project.getKey(), repositorySlug)  + "?limit=" + requestSize + "&start=" + firstPagingElement)
                        .header("Authorization", webUtils.getBasicAuthString())
                        .exchange();

                repository = (BitBucketRepository) webUtils.convertResponse(response, new TypeReference<BitBucketRepository>(){});
        } catch (Exception e) {
            ExitUtils.repositoryNotFetched();
        }
        return repository;
    }
}
