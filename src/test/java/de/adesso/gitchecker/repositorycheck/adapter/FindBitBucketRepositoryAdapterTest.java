package de.adesso.gitchecker.repositorycheck.adapter;

import de.adesso.gitchecker.repositorycheck.domain.BitBucketRepository;
import de.adesso.gitchecker.repositorycheck.domain.BitBucketResources;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
class FindBitBucketRepositoryAdapterTest {

    @Autowired
    private FindBitBucketRepositoryAdapter findRepositories;
    @Autowired
    private BitBucketResources resources;

    @Test
    void repositoriesBeingLoaded() {
        //When
        List<BitBucketRepository> repositories = findRepositories.byProject(resources.getProject());

        //Then
        Assertions.assertThat(repositories.size()).isGreaterThan(0);
    }

    @Test
    void repositoryBeingLoaded() {
        //When
        BitBucketRepository repository = findRepositories.byProjectAndSlug(resources.getProject(), resources.getTargetRepository().getSlug());

        //Then
        Assertions.assertThat(repository.getName()).isNotEmpty();
    }
}
