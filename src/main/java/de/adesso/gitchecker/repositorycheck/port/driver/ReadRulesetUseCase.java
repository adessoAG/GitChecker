package de.adesso.gitchecker.repositorycheck.port.driver;

import de.adesso.gitchecker.repositorycheck.domain.Ruleset;

public interface ReadRulesetUseCase {

    Ruleset read();
}
