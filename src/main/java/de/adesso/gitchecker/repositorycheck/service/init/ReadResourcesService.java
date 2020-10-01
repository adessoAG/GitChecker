package de.adesso.gitchecker.repositorycheck.service.init;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adesso.gitchecker.repositorycheck.domain.BitBucketResources;
import de.adesso.gitchecker.repositorycheck.port.driven.ReadConfigFilePort;
import de.adesso.gitchecker.repositorycheck.port.driver.ReadResourcesUseCase;
import de.adesso.gitchecker.repositorycheck.utils.ExitUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReadResourcesService implements ReadResourcesUseCase {

    private final ObjectMapper mapper;
    private final ReadConfigFilePort configFile;

    @Override
    public BitBucketResources read() {
        BitBucketResources resources = null;
        try {
            String jsonConfig = configFile.readContent();
            resources = mapper.readValue(jsonConfig, BitBucketResources.class);
            checkResources(resources);
        } catch (Exception e) {
            ExitUtils.resourcesNotParsed();
        }
        return resources;
    }

    private void checkResources(BitBucketResources resources) {
        if (resources.getServerURL().isEmpty()
                || resources.getProject().getKey().isEmpty()
                || resources.getTargetRepository().getSlug().isEmpty()) {
            ExitUtils.resourcesNotParsed();
        }
    }
}
