package de.adesso.gitchecker.repositorycheck.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.adesso.gitchecker.repositorycheck.domain.BitBucketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

import java.util.Base64;

@Component
@RequiredArgsConstructor
public class WebUtils {

    @Value("${user}")
    private String username;
    @Value("${password}")
    private String password;

    @Value("${url.bitbucket.resource.projects}")
    private String projectURL;
    @Value("${url.bitbucket.resource.projects.repositories}")
    private String repositoryURL;
    @Value("${url.bitbucket.resource.projects.repositories.branches}")
    private String branchURL;
    @Value("${url.bitbucket.resource.projects.repositories.commits}")
    private String commitURL;
    @Value("${url.bitbucket.resource.projects.repositories.commits.distinct}")
    private String distinctCommitURL;
    @Value("${url.bitbucket.resource.projects.repositories.pull-requests}")
    private String pullRequestURL;

    private final ObjectMapper mapper;

    public String getBasicAuthString() {
        String digestSubject = username + ":" + password;
        String digestResult = Base64.getEncoder().encodeToString(digestSubject.getBytes());
        return ("Basic " + digestResult + "=").replace("==", "=");
    }

    public Object convertResponse(Mono<ClientResponse> response, TypeReference resultType) throws JsonProcessingException {
        String jsonResult = response.flatMap(res -> res.bodyToMono(String.class)).block();
        if (jsonResult.contains("AuthenticationException")) {
            ExitUtils.authenticationFailed();
        }
        return mapper.readValue(jsonResult, resultType);
    }

    public String getProjectURL() {
        return projectURL;
    }

    public String getProjectURL(String projectKey) {
        return projectURL + projectKey;
    }

    public String getRepositoryURL(String projectKey) {
        return getProjectURL(projectKey) + repositoryURL;
    }

    public String getRepositoryURL(String projectKey, String repositorySlug) {
        return getRepositoryURL(projectKey) + repositorySlug;
    }

    public String getBranchURL(BitBucketRepository repository) {
        return getRepositoryURL(repository.getProject().getKey(), repository.getSlug()) + branchURL;
    }

    public String getPullRequestURL(BitBucketRepository repository) {
        return getRepositoryURL(repository.getProject().getKey(), repository.getSlug()) + pullRequestURL;
    }

    public String getCommitURL(BitBucketRepository repository) {
        return getRepositoryURL(repository.getProject().getKey(), repository.getSlug()) + commitURL;
    }

    public String getDistinctCommitURL(BitBucketRepository repository) {
        return getRepositoryURL(repository.getProject().getKey(), repository.getSlug()) + distinctCommitURL;
    }
}
