package de.adesso.gitchecker.repositorycheck.adapter;

import com.fasterxml.jackson.core.type.TypeReference;
import de.adesso.gitchecker.repositorycheck.domain.BitBucketPagingResponse;
import de.adesso.gitchecker.repositorycheck.domain.BitBucketRepository;
import de.adesso.gitchecker.repositorycheck.domain.Branch;
import de.adesso.gitchecker.repositorycheck.port.driven.FindBitBucketBranchPort;
import de.adesso.gitchecker.repositorycheck.utils.ExitUtils;
import de.adesso.gitchecker.repositorycheck.utils.WebUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class FindBitBucketBranchAdapter implements FindBitBucketBranchPort {

    @Value("${webclient.requestSize}")
    private Integer requestSize;

    private final WebUtils webUtils;
    private final WebClient client;

    @Override
    public Map<String, Branch> byRepository(BitBucketRepository repository) {
        Map<String, Branch> branches = new HashMap<>();
        try {
            Integer firstPagingElement = 0;
            BitBucketPagingResponse pagingResponse;
            do {
                Mono<ClientResponse> response = client.get()
                        .uri(webUtils.getBranchURL(repository) + "?limit=" + requestSize + "&start=" + firstPagingElement)
                        .header("Authorization", webUtils.getBasicAuthString())
                        .exchange();

                pagingResponse = (BitBucketPagingResponse) webUtils.convertResponse(response, new TypeReference<BitBucketPagingResponse<Branch>>(){});
                ((List<Branch>) pagingResponse.getValues()).forEach(branch -> branches.put(branch.getName(), branch));
                firstPagingElement = pagingResponse.getNextStartPage();
            } while (!pagingResponse.getIsLastPage());
        } catch (Exception e) {
            ExitUtils.branchesNotFetched();
        }
        return branches;
    }
}
