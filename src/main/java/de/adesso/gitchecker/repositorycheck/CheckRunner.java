package de.adesso.gitchecker.repositorycheck;

import de.adesso.gitchecker.repositorycheck.port.driver.PerformRepositoryCheckUseCase;
import de.adesso.gitchecker.repositorycheck.utils.ExitUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "check.run", havingValue = "true")
public class CheckRunner implements ApplicationRunner {

    private final ApplicationContext context;
    private final PerformRepositoryCheckUseCase repositoryCheck;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (ExitUtils.getExitCode() != 0) {
            return;
        }
        ExitUtils.context = context;

        repositoryCheck.perform();
    }
}
