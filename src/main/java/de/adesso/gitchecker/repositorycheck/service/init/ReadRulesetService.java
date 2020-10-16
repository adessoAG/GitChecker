package de.adesso.gitchecker.repositorycheck.service.init;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adesso.gitchecker.repositorycheck.domain.Ruleset;
import de.adesso.gitchecker.repositorycheck.port.driven.ReadConfigFilePort;
import de.adesso.gitchecker.repositorycheck.port.driver.ReadRulesetUseCase;
import de.adesso.gitchecker.repositorycheck.utils.ExitUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class ReadRulesetService implements ReadRulesetUseCase {

    private final ObjectMapper mapper;
    private final ReadConfigFilePort configFile;

    @Override
    public Ruleset read() {
        Ruleset ruleset = null;
        try {
            String jsonConfig = configFile.readContent();
            ruleset = mapper.readValue(jsonConfig, Ruleset.class);
            checkRulesetValid(ruleset);
        } catch (Exception e) {
            ExitUtils.rulesetNotParsed();
        }
        return ruleset;
    }

    private void checkRulesetValid(Ruleset ruleset) {
        checkBranchOriginsTypes(ruleset);
        checkBranchMergesTypes(ruleset);
        checkBranchStalePeriodTypes(ruleset);
    }

    private void checkBranchStalePeriodTypes(Ruleset ruleset) {
        if (nonNull(ruleset.getBranchStalePeriods())) {
            ruleset.getBranchStalePeriods().forEach((key, value) -> {
                if (!ruleset.branchTypes().contains(key)) {
                    ExitUtils.rulesetInvalid("Branch type used in stale periods not declared: "+ key);
                }
            });
        }
    }

    private void checkBranchMergesTypes(Ruleset ruleset) {
        if (nonNull(ruleset.getAllowedBranchMerges())) {
            ruleset.getAllowedBranchMerges().forEach((key, values) -> {
                if (!ruleset.branchTypes().contains(key)) {
                    ExitUtils.rulesetInvalid("Branch type used in allowed merges key not declared: "+ key + ": " + values);
                }
                values.forEach(value -> {
                    if (!ruleset.branchTypes().contains(value) && !value.equalsIgnoreCase("ORIGIN")) {
                        ExitUtils.rulesetInvalid("Branch type used in allowed merges target not declared: "+ key + ": " + values);
                    }
                });
            });
        }
    }

    private void checkBranchOriginsTypes(Ruleset ruleset) {
        if (nonNull(ruleset.getAllowedBranchOrigins())) {
            ruleset.getAllowedBranchOrigins().forEach((key, values) -> {
                if (!ruleset.branchTypes().contains(key) || !ruleset.branchTypes().containsAll(values)) {
                    ExitUtils.rulesetInvalid("Branch type used in allowed origins not declared: "+ key + ": " + values);
                }
            });
        }
    }
}
