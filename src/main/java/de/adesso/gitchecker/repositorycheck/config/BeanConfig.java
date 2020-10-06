package de.adesso.gitchecker.repositorycheck.config;

import de.adesso.gitchecker.repositorycheck.domain.BitBucketResources;
import de.adesso.gitchecker.repositorycheck.domain.Ruleset;
import de.adesso.gitchecker.repositorycheck.port.driver.ReadResourcesUseCase;
import de.adesso.gitchecker.repositorycheck.port.driver.ReadRulesetUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class BeanConfig {

    @Value("${webclient.codec.buffersize}")
    private Integer bufferMBs;

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
        return WebClient.builder().exchangeStrategies(ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize( bufferMBs * 1024 * 1024)).build())
                .baseUrl(resources.getServerURL())
                .build();
    }
}
