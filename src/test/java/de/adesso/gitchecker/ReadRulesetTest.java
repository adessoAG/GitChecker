package de.adesso.gitchecker;

import de.adesso.gitchecker.repositorycheck.domain.Ruleset;
import de.adesso.gitchecker.repositorycheck.port.driver.ReadRulesetUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
class ReadRulesetTest {

    @Autowired
    private ReadRulesetUseCase readRuleset;

    @Test
    void rulesetIsParsedCorrectly() {
        //When
        Ruleset ruleset = readRuleset.read();

        //Then
        assertThat(ruleset.getBranchRemovalAfterPRMerge())
                .isEqualTo(true);

        assertThat(ruleset.getBranchNamingPatterns())
                .containsOnlyKeys(
                        "DEVELOP",
                        "MASTER",
                        "FEATURE",
                        "BUGFIX",
                        "DEVELOPER");

        assertThat(ruleset.getAllowedBranchOrigins())
                .containsOnlyKeys(
                        "DEVELOP",
                        "FEATURE",
                        "BUGFIX",
                        "DEVELOPER");

        assertThat(ruleset.getAllowedBranchMerges())
                .containsOnlyKeys(
                        "DEVELOP",
                        "FEATURE",
                        "BUGFIX",
                        "DEVELOPER");

        assertThat(ruleset.getBranchStalePeriods())
                .containsOnly(
                        entry("FEATURE", 90),
                        entry("BUGFIX", 14),
                        entry("DEVELOPER", 30));
    }
}

