package de.adesso.gitchecker;

import de.adesso.gitchecker.repositorycheck.utils.WebUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
class DigestTest {

    @Autowired
    private WebUtils webUtils;
    @Autowired
    Environment environment;

    @Test
    void checkBasicAuthString() {
        //Given
        String expected = environment.getProperty("bitbucket.authstring");

        //When
        String result = webUtils.getBasicAuthString();

        //Then
        Assertions.assertEquals(expected, result);
    }
}
