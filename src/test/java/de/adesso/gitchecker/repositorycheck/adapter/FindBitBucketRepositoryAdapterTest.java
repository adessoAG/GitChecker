package de.adesso.gitchecker.repositorycheck.adapter;

import de.adesso.gitchecker.repositorycheck.domain.BitBucketRepository;
import de.adesso.gitchecker.repositorycheck.domain.BitBucketResources;
import de.adesso.gitchecker.repositorycheck.utils.BitBucketServerDummy;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
    @Autowired
    private BitBucketServerDummy serverDummy;

    @BeforeEach
    void setUp() {
        serverDummy.init();
    }

    @AfterEach
    void shutDown() {
        serverDummy.kill();
    }

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
