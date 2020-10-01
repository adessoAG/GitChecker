package de.adesso.gitchecker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.adesso.gitchecker.repositorycheck.domain.*;
import de.adesso.gitchecker.repositorycheck.utils.WebUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
class WebClientTest {

  @Autowired
  private ObjectMapper mapper;
  @Autowired
  private WebUtils webUtils;

  @Test
  public void fetchBitBucketProject() throws JsonProcessingException {

    WebClient client = WebClient.create("https://bitbucket.adesso-group.com");

    Mono<ClientResponse> response = client.get().uri("/rest/api/1.0/projects").header("Authorization", webUtils.getBasicAuthString()).exchange();
    String result = response.flatMap(res -> res.bodyToMono(String.class)).block();

    BitBucketPagingResponse b = mapper.readValue(result, new TypeReference<BitBucketPagingResponse<BitBucketProject>>(){});
    System.out.println(b.getValues().get(0));
  }

  @Test
  public void fetchBitBucketRepository() throws JsonProcessingException {

    WebClient client = WebClient.create("https://bitbucket.adesso-group.com");

    Mono<ClientResponse> response = client.get().uri("/rest/api/1.0/projects/PMD/repos").header("Authorization", webUtils.getBasicAuthString()).exchange();
    String result = response.flatMap(res -> res.bodyToMono(String.class)).block();
    BitBucketPagingResponse b = mapper.readValue(result, new TypeReference<BitBucketPagingResponse<BitBucketRepository>>(){});

    System.out.println(b.getValues());
  }

  @Test
  public void fetchBitBucketBranches() throws JsonProcessingException {

    WebClient client = WebClient.create("https://bitbucket.adesso-group.com");

    Mono<ClientResponse> response = client.get().uri("/rest/api/1.0/projects/PMD/repos/backend/branches").header("Authorization", webUtils.getBasicAuthString()).exchange();
    String result = response.flatMap(res -> res.bodyToMono(String.class)).block();
    BitBucketPagingResponse b = mapper.readValue(result, new TypeReference<BitBucketPagingResponse>(){});

    System.out.println(b.getValues());
  }

  @Test
  public void fetchBitBucketCommits() throws JsonProcessingException {

    WebClient client = WebClient.create("https://bitbucket.adesso-group.com");

    Mono<ClientResponse> response = client.get().uri("/rest/api/1.0/projects/PMD/repos/backend/commits").header("Authorization", webUtils.getBasicAuthString()).exchange();
    String result = response.flatMap(res -> res.bodyToMono(String.class)).block();
    BitBucketPagingResponse b = mapper.readValue(result, new TypeReference<BitBucketPagingResponse<Commit>>(){});

    System.out.println(b.getValues());
  }

  @Test
  public void fetchBitBucketPullRequests() throws JsonProcessingException {

    WebClient client = WebClient.create("https://bitbucket.adesso-group.com");

    Mono<ClientResponse> response = client.get().uri("/rest/api/1.0/projects/PMD/repos/backend/pull-requests?state=ALL").header("Authorization", webUtils.getBasicAuthString()).exchange();
    String result = response.flatMap(res -> res.bodyToMono(String.class)).block();
    BitBucketPagingResponse b = mapper.readValue(result, new TypeReference<BitBucketPagingResponse<PullRequest>>(){});

    System.out.println(b.getValues());
  }
}
