package de.adesso.gitchecker;

import de.adesso.gitchecker.repositorycheck.domain.BitBucketResources;
import de.adesso.gitchecker.repositorycheck.port.driver.ReadResourcesUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
class ReadResourcesTest {

    @Autowired
    private ReadResourcesUseCase readResources;

    @Test
    void resourcesParsedCorrectly() {
        //When
        BitBucketResources resources = readResources.read();

        //Then
        assertThat(resources.getServerURL())
                .isEqualTo("https://bitbucket.adesso-group.com");

        assertThat(resources.getProject().getKey())
                .isEqualTo("PMD");

        assertThat(resources.getTargetRepository().getSlug())
                .isEqualTo("backend");
    }
}
