package de.adesso.gitchecker.repositorycheck.config;

import de.adesso.gitchecker.repositorycheck.domain.BitBucketResources;
import de.adesso.gitchecker.repositorycheck.domain.Ruleset;
import de.adesso.gitchecker.repositorycheck.port.driver.ReadResourcesUseCase;
import de.adesso.gitchecker.repositorycheck.port.driver.ReadRulesetUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class BeanConfig {

    private final ReadRulesetUseCase readRuleset;
    private final ReadResourcesUseCase readResources;

    @Bean
    public Ruleset ruleset() {
        return readRuleset.read();
    }

    @Bean
    public BitBucketResources resources() {
        return readResources.read();
    }

    @Bean
    public WebClient webClient(BitBucketResources resources) {
        return WebClient.create(resources.getServerURL());
    }
}
