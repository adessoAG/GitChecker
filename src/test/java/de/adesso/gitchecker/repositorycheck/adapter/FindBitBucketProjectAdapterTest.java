package de.adesso.gitchecker.repositorycheck.adapter;

import de.adesso.gitchecker.repositorycheck.domain.BitBucketProject;
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
class FindBitBucketProjectAdapterTest {

    @Autowired
    private FindBitBucketProjectAdapter findProjects;
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
    void projectsBeingFetched() {
        //When
        List<BitBucketProject> projects = findProjects.findAll();

        //Then
        Assertions.assertThat(projects.size()).isGreaterThan(0);
    }

    @Test
    void projectBeingFetched() {
        //When
        BitBucketProject project = findProjects.byProjectKey(resources.getProject().getKey());

        //Then
        Assertions.assertThat(project.getName()).isNotEmpty();
    }
}
