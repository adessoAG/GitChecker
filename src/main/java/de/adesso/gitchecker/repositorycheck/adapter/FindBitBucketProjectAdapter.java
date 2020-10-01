package de.adesso.gitchecker.repositorycheck.adapter;

import com.fasterxml.jackson.core.type.TypeReference;
import de.adesso.gitchecker.repositorycheck.domain.BitBucketPagingResponse;
import de.adesso.gitchecker.repositorycheck.domain.BitBucketProject;
import de.adesso.gitchecker.repositorycheck.port.driven.FindBitBucketProjectPort;
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
public class FindBitBucketProjectAdapter implements FindBitBucketProjectPort {

    @Value("${webclient.requestSize}")
    private Integer requestSize;

    private final WebUtils webUtils;
    private final WebClient client;

    @Override
    public List<BitBucketProject> findAll() {
        List<BitBucketProject> projects = new ArrayList<>();
        try {
            Integer firstPagingElement = 0;
            BitBucketPagingResponse pagingResponse;
            do {
                Mono<ClientResponse> response = client.get()
                        .uri(webUtils.getProjectURL() + "?limit=" + requestSize + "&start=" + firstPagingElement)
                        .header("Authorization", webUtils.getBasicAuthString())
                        .exchange();

                pagingResponse = (BitBucketPagingResponse) webUtils.convertResponse(response, new TypeReference<BitBucketPagingResponse<BitBucketProject>>(){});
                projects.addAll(pagingResponse.getValues());
                firstPagingElement = pagingResponse.getNextStartPage();
            } while (!pagingResponse.getIsLastPage());
        } catch (Exception e) {
            ExitUtils.projectNotFetched();
        }
        return projects;
    }

    @Override
    public BitBucketProject byProjectKey(String projectKey) {
        BitBucketProject project = null;
        try {
            Mono<ClientResponse> response = client.get()
                    .uri(webUtils.getProjectURL(projectKey))
                    .header("Authorization", webUtils.getBasicAuthString())
                    .exchange();

            project = (BitBucketProject) webUtils.convertResponse(response, new TypeReference<BitBucketProject>(){});
        } catch (Exception e) {
            ExitUtils.projectNotFetched();
        }
        return project;
    }
}

